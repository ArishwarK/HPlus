import express from 'express';
import { createServer as createViteServer } from 'vite';
import mysql from 'mysql2/promise';
import fs from 'fs';
import path from 'path';

const PORT = Number(process.env.PORT) || 3000;

const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  port: Number(process.env.DB_PORT) || 3306,
  user: process.env.DB_USERNAME || 'root',
  password: process.env.DB_PASSWORD || 'arish2007',
  database: process.env.DB_NAME || 'hospital_queue_db',
  multipleStatements: true,
};

let pool: mysql.Pool | null = null;
let mysqlConnected = false;

async function initMySQL() {
  try {
    // 1. Connect to MySQL server root to create hospital_queue_db if it doesn't exist
    const rootConn = await mysql.createConnection({
      host: dbConfig.host,
      port: dbConfig.port,
      user: dbConfig.user,
      password: dbConfig.password,
      multipleStatements: true,
    });

    await rootConn.query(
      `CREATE DATABASE IF NOT EXISTS \`${dbConfig.database}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
    );
    await rootConn.end();

    // 2. Create connection pool to hospital_queue_db
    pool = mysql.createPool(dbConfig);

    // 3. Run schema.sql and seed_data.sql if tables are empty
    const schemaPath = path.resolve(process.cwd(), 'src/main/resources/schema.sql');
    const seedPath = path.resolve(process.cwd(), 'src/main/resources/seed_data.sql');

    if (fs.existsSync(schemaPath)) {
      const schemaSql = fs.readFileSync(schemaPath, 'utf8');
      await pool.query(schemaSql);
    }

    const [rows] = await pool.query<mysql.RowDataPacket[]>('SELECT COUNT(*) AS cnt FROM users');
    if (rows[0].cnt === 0 && fs.existsSync(seedPath)) {
      const seedSql = fs.readFileSync(seedPath, 'utf8');
      await pool.query(seedSql);
    }

    mysqlConnected = true;
    console.log('✅ Connected to MySQL (hospital_queue_db) and verified schema & seed data.');
  } catch (err) {
    mysqlConnected = false;
    console.log('ℹ️ Local MySQL not detected in this environment; using built-in storage fallback.');
  }
}

async function startServer() {
  await initMySQL();

  const app = express();
  app.use(express.json());

  // API: Check DB status
  app.get('/api/db-status', (_req, res) => {
    res.json({ connected: mysqlConnected, database: dbConfig.database });
  });

  // API: Register Walk-In Patient into MySQL
  app.post('/api/walkin', async (req, res) => {
    if (!mysqlConnected || !pool) {
      return res.json({ savedToMySQL: false });
    }
    try {
      const { tokenNumber, tokenDisplay, patientName, patientUhid, phone, priority, score, doctorId } = req.body;
      const [patientResult] = await pool.query<mysql.ResultSetHeader>(
        `INSERT INTO patients (uhid, full_name, gender, date_of_birth, phone_number, blood_group, address)
         VALUES (?, ?, 'OTHER', '1990-01-01', ?, 'O+', 'Walk-In Registration')
         ON DUPLICATE KEY UPDATE full_name = VALUES(full_name)`,
        [patientUhid, patientName, phone || '+1-555-0199']
      );
      const patientId = patientResult.insertId || 1;

      await pool.query(
        `INSERT INTO queue_entries (token_number, token_display, doctor_id, patient_id, queue_date, priority_level, calculated_priority_score, status, arrival_time)
         VALUES (?, ?, ?, ?, CURRENT_DATE(), ?, ?, 'WAITING', NOW())
         ON DUPLICATE KEY UPDATE status = VALUES(status)`,
        [tokenNumber, String(tokenDisplay), doctorId || 1, patientId, priority || 'NORMAL', score || 100]
      );

      res.json({ savedToMySQL: true, patientId });
    } catch (err: any) {
      res.status(500).json({ savedToMySQL: false, error: err.message });
    }
  });

  // API: Update token status in MySQL
  app.post('/api/queue/status', async (req, res) => {
    if (!mysqlConnected || !pool) {
      return res.json({ savedToMySQL: false });
    }
    try {
      const { tokenNumber, status } = req.body;
      await pool.query(
        `UPDATE queue_entries SET status = ?, updated_at = NOW() WHERE token_number = ?`,
        [status, tokenNumber]
      );
      res.json({ savedToMySQL: true });
    } catch (err: any) {
      res.status(500).json({ savedToMySQL: false, error: err.message });
    }
  });

  // API: Save consultation & prescription in MySQL
  app.post('/api/consultation', async (req, res) => {
    if (!mysqlConnected || !pool) {
      return res.json({ savedToMySQL: false });
    }
    try {
      const { queueId, tokenNumber, diagnosis, prescription } = req.body;
      await pool.query(
        `UPDATE queue_entries SET status = 'COMPLETED', consultation_end_time = NOW() WHERE token_number = ?`,
        [tokenNumber]
      );
      await pool.query(
        `INSERT INTO consultations (queue_id, doctor_id, patient_id, chief_complaints, diagnosis, prescription, duration_minutes, consultation_date)
         VALUES (?, 1, 1, 'Outpatient Consultation', ?, ?, 8, CURRENT_DATE())
         ON DUPLICATE KEY UPDATE diagnosis = VALUES(diagnosis), prescription = VALUES(prescription)`,
        [queueId || 1, diagnosis, prescription]
      );
      res.json({ savedToMySQL: true });
    } catch (err: any) {
      res.status(500).json({ savedToMySQL: false, error: err.message });
    }
  });

  const vite = await createViteServer({
    server: { middlewareMode: true },
    appType: 'spa',
  });
  app.use(vite.middlewares);

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

startServer();
