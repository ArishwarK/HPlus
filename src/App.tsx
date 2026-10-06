/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import {
  Activity,
  Users,
  Calendar,
  Clock,
  UserCheck,
  Stethoscope,
  Building2,
  FileText,
  Shield,
  Layers,
  Code2,
  Bell,
  Play,
  CheckCircle2,
  SkipForward,
  RotateCcw,
  Sparkles,
  ArrowRight,
  Database,
  Server,
  Terminal,
  LogOut,
  AlertTriangle,
  ChevronRight,
  ExternalLink,
  Megaphone,
  Volume2,
  VolumeX,
  LogIn,
  Lock,
  Key,
  User,
  Phone,
  Check,
  RefreshCw,
  Send,
  Plus,
  Radio,
  Info,
  ShieldAlert
} from 'lucide-react';
import { RoleLoginPage } from './RoleLoginPage';

// Role-Based Access Control (RBAC) Types
export type UserRole = 'PATIENT' | 'DOCTOR' | 'RECEPTIONIST' | 'ADMIN';
export type PortalType = 'PATIENT' | 'DOCTOR' | 'RECEPTIONIST' | 'ADMIN' | 'ANNOUNCEMENTS';

// Access Control Rules:
// - Patient: Can ONLY access Patient Portal and Announcement Board
// - Doctor: Can ONLY access Doctor OPD and Announcement Board
// - Reception desk: Can ONLY access Reception Desk and Announcement Board
// - Admin: Can access ALL portals (Patient, Doctor, Receptionist, Admin, Announcements)
export const ROLE_PERMISSIONS: Record<UserRole, PortalType[]> = {
  PATIENT: ['PATIENT', 'ANNOUNCEMENTS'],
  DOCTOR: ['DOCTOR', 'ANNOUNCEMENTS'],
  RECEPTIONIST: ['RECEPTIONIST', 'ANNOUNCEMENTS'],
  ADMIN: ['PATIENT', 'DOCTOR', 'RECEPTIONIST', 'ADMIN', 'ANNOUNCEMENTS']
};

// Priority Levels matching com.hospital.model.PriorityLevel
type Priority = 'NORMAL' | 'HIGH' | 'EMERGENCY';

interface QueueItem {
  queueId: number;
  tokenNumber: number;
  tokenDisplay: string;
  patientName: string;
  patientUhid: string;
  departmentCode: string;
  doctorName: string;
  priority: Priority;
  score: number;
  status: 'WAITING' | 'CALLED' | 'IN_CONSULTATION' | 'COMPLETED' | 'SKIPPED';
  arrivalTime: string;
  waitMinutes: number;
  symptoms: string;
}

interface Doctor {
  id: number;
  name: string;
  dept: string;
  deptCode: string;
  room: string;
  avgMinutes: number;
  status: 'AVAILABLE' | 'BUSY' | 'ON_BREAK';
}

interface HospitalAnnouncement {
  id: number;
  title: string;
  category: 'URGENT' | 'OPD' | 'GENERAL' | 'CLINICAL';
  message: string;
  time: string;
  author: string;
  isPinned?: boolean;
}

export default function App() {
  // Session Authentication State (Starts false: opens the 4-role login page first)
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
  const [loginSelectedRole, setLoginSelectedRole] = useState<UserRole>('PATIENT');

  // Current Active User Role Session (PATIENT | DOCTOR | RECEPTIONIST | ADMIN)
  const [currentUserRole, setCurrentUserRole] = useState<UserRole>('PATIENT');

  // Navigation: 5 Distinct Portals
  const [activePortal, setActivePortal] = useState<PortalType>('PATIENT');

  // Dedicated Authentication States for each role
  const [patientAuth, setPatientAuth] = useState<{
    isAuthenticated: boolean;
    tokenNumber: number;
    tokenDisplay: string;
    patientName: string;
    patientUhid: string;
    phone: string;
  }>({
    isAuthenticated: false,
    tokenNumber: 45,
    tokenDisplay: '45',
    patientName: 'Robert Chang',
    patientUhid: 'UHID-2026-0045',
    phone: '+1 (555) 234-5678'
  });

  const [doctorAuth, setDoctorAuth] = useState<{
    isAuthenticated: boolean;
    doctorId: number;
    doctorName: string;
    dept: string;
    room: string;
  }>({
    isAuthenticated: false,
    doctorId: 1,
    doctorName: 'Dr. Rajesh Kumar',
    dept: 'Cardiology',
    room: 'OPD-204'
  });

  const [receptionistAuth, setReceptionistAuth] = useState<{
    isAuthenticated: boolean;
    staffId: string;
    staffName: string;
    counter: string;
  }>({
    isAuthenticated: false,
    staffId: 'REC-4012',
    staffName: 'Sarah Jenkins',
    counter: 'Counter 1 (Central OPD Registration)'
  });

  const [adminAuth, setAdminAuth] = useState<{
    isAuthenticated: boolean;
    username: string;
    roleTitle: string;
  }>({
    isAuthenticated: false,
    username: 'admin@hospital.org',
    roleTitle: 'Hospital Operations Director'
  });

  // Separate Login Form Fields
  // 1. Patient Login
  const [patientLoginToken, setPatientLoginToken] = useState('45');
  const [patientLoginPhone, setPatientLoginPhone] = useState('+1 (555) 234-5678');
  const [patientLoginTab, setPatientLoginTab] = useState<'TOKEN' | 'UHID'>('TOKEN');
  const [patientLoginUhid, setPatientLoginUhid] = useState('UHID-2026-0045');

  // 2. Doctor Login
  const [docLoginUser, setDocLoginUser] = useState('dr.kumar@hospital.org');
  const [docLoginPass, setDocLoginPass] = useState('••••••••');
  const [docLoginRoom, setDocLoginRoom] = useState('1');

  // 3. Receptionist Login
  const [recLoginUser, setRecLoginUser] = useState('REC-4012');
  const [recLoginPass, setRecLoginPass] = useState('••••••••');
  const [recLoginCounter, setRecLoginCounter] = useState('1');

  // 4. Admin Login
  const [adminLoginUser, setAdminLoginUser] = useState('admin@hospital.org');
  const [adminLoginPass, setAdminLoginPass] = useState('••••••••');
  const [adminLoginScope, setAdminLoginScope] = useState('FULL');

  // Real-time Queue State
  const [doctors, setDoctors] = useState<Doctor[]>([
    { id: 1, name: 'Dr. Rajesh Kumar', dept: 'Cardiology', deptCode: 'CARD', room: 'OPD-204', avgMinutes: 8, status: 'BUSY' },
    { id: 2, name: 'Dr. Elena Rostova', dept: 'Orthopedics', deptCode: 'ORTH', room: 'OPD-112', avgMinutes: 12, status: 'AVAILABLE' },
    { id: 3, name: 'Dr. Marcus Vance', dept: 'Pediatrics', deptCode: 'PED', room: 'OPD-305', avgMinutes: 8, status: 'AVAILABLE' },
    { id: 4, name: 'Dr. Aisha Patel', dept: 'General Medicine', deptCode: 'GEN', room: 'OPD-101', avgMinutes: 8, status: 'AVAILABLE' },
  ]);

  const [queue, setQueue] = useState<QueueItem[]>([
    { queueId: 1, tokenNumber: 41, tokenDisplay: '41', patientName: 'Johnathan Doe', patientUhid: 'UHID-2026-0041', departmentCode: 'CARD', doctorName: 'Dr. Rajesh Kumar', priority: 'NORMAL', score: 100, status: 'COMPLETED', arrivalTime: '08:45 AM', waitMinutes: 0, symptoms: 'Chest tightness upon brisk walking' },
    { queueId: 2, tokenNumber: 42, tokenDisplay: '42', patientName: 'Maria Santos', patientUhid: 'UHID-2026-0042', departmentCode: 'CARD', doctorName: 'Dr. Rajesh Kumar', priority: 'HIGH', score: 180, status: 'CALLED', arrivalTime: '09:05 AM', waitMinutes: 0, symptoms: 'Sharp chest ache upon deep breathing' },
    { queueId: 3, tokenNumber: 43, tokenDisplay: '43', patientName: 'David Kim', patientUhid: 'UHID-2026-0043', departmentCode: 'CARD', doctorName: 'Dr. Rajesh Kumar', priority: 'NORMAL', score: 115, status: 'WAITING', arrivalTime: '09:12 AM', waitMinutes: 8, symptoms: 'Routine 6-month lipid and cardio review' },
    { queueId: 4, tokenNumber: 44, tokenDisplay: '44', patientName: 'Eleanor Vance', patientUhid: 'UHID-2026-0044', departmentCode: 'CARD', doctorName: 'Dr. Rajesh Kumar', priority: 'NORMAL', score: 110, status: 'WAITING', arrivalTime: '09:20 AM', waitMinutes: 16, symptoms: 'Shortness of breath climbing stairs' },
    { queueId: 5, tokenNumber: 45, tokenDisplay: '45', patientName: 'Robert Chang', patientUhid: 'UHID-2026-0045', departmentCode: 'CARD', doctorName: 'Dr. Rajesh Kumar', priority: 'NORMAL', score: 105, status: 'WAITING', arrivalTime: '09:28 AM', waitMinutes: 24, symptoms: 'Blood pressure checkup and medication refill' },
  ]);

  // Announcements State
  const [announcements, setAnnouncements] = useState<HospitalAnnouncement[]>([
    {
      id: 1,
      title: 'Emergency Triage Protocol In Effect',
      category: 'URGENT',
      message: 'Trauma & acute cardiac emergency cases are given immediate consultation priority as per NABH clinical guidelines.',
      time: '10 mins ago',
      author: 'Medical Superintendent',
      isPinned: true
    },
    {
      id: 2,
      title: 'Wheelchair Escort & Senior Citizen Priority',
      category: 'GENERAL',
      message: 'Free wheelchair escorts and fast-track token assistance are available at Central Reception Counter 2.',
      time: '35 mins ago',
      author: 'Front Desk Supervisor'
    },
    {
      id: 3,
      title: 'Cardiology OPD Schedule',
      category: 'OPD',
      message: 'Dr. Rajesh Kumar OPD-204 is actively serving tokens 41 through 55. Average wait time is currently 8 minutes.',
      time: '1 hour ago',
      author: 'Cardiology OPD Nursing'
    },
    {
      id: 4,
      title: 'Central Pharmacy Dispensing Counter 4',
      category: 'CLINICAL',
      message: 'Following doctor consultation, collect digital prescriptions directly at Ground Floor Pharmacy Counter 4.',
      time: '2 hours ago',
      author: 'Chief Pharmacist'
    }
  ]);

  // Audio Chime & Speech Synthesis Settings
  const [audioEnabled, setAudioEnabled] = useState<boolean>(true);
  const [broadcastModalOpen, setBroadcastModalOpen] = useState<boolean>(false);
  const [newNoticeTitle, setNewNoticeTitle] = useState('');
  const [newNoticeCategory, setNewNoticeCategory] = useState<'URGENT' | 'OPD' | 'GENERAL' | 'CLINICAL'>('OPD');
  const [newNoticeMsg, setNewNoticeMsg] = useState('');

  // Doctor Consultation Modal State
  const [showConsultModal, setShowConsultModal] = useState<boolean>(false);
  const [activeConsultToken, setActiveConsultToken] = useState<QueueItem | null>(null);
  const [diagnosisText, setDiagnosisText] = useState('');
  const [prescriptionText, setPrescriptionText] = useState('');

  // Receptionist Walk-in Registration State
  const [walkinName, setWalkinName] = useState('');
  const [walkinPhone, setWalkinPhone] = useState('');
  const [walkinPriority, setWalkinPriority] = useState<Priority>('NORMAL');
  const [walkinDoctorId, setWalkinDoctorId] = useState<number>(1);

  // Toast notification
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage((current) => (current === msg ? null : current));
    }, 3500);
  };

  // Real-time Clock for Announcement TV Display
  const [currentTime, setCurrentTime] = useState<string>(new Date().toLocaleTimeString());
  const [currentDate, setCurrentDate] = useState<string>(
    new Date().toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
  );

  // Auto-refresh simulation ticker (Simulates 5s periodic AJAX polling)
  const [lastSyncTime, setLastSyncTime] = useState<string>(new Date().toLocaleTimeString());
  const [pollCounter, setPollCounter] = useState<number>(5);

  useEffect(() => {
    const clockInterval = setInterval(() => {
      const now = new Date();
      setCurrentTime(now.toLocaleTimeString());
    }, 1000);
    return () => clearInterval(clockInterval);
  }, []);

  useEffect(() => {
    const interval = setInterval(() => {
      setPollCounter((prev) => {
        if (prev <= 1) {
          setLastSyncTime(new Date().toLocaleTimeString());
          return 5;
        }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  // Web Audio API Hospital 2-Tone Chime
  const playHospitalChime = () => {
    try {
      const AudioCtxClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      if (!AudioCtxClass) return;
      const ctx = new AudioCtxClass();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();

      osc.type = 'sine';
      // Airport/hospital dual chime: A5 (880Hz) followed by D5 (587.3Hz)
      osc.frequency.setValueAtTime(880, ctx.currentTime);
      osc.frequency.setValueAtTime(587.33, ctx.currentTime + 0.22);

      gain.gain.setValueAtTime(0.18, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.85);

      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 0.85);
    } catch {
      // AudioContext unavailable or blocked by browser
    }
  };

  // Announce token with chime and voice
  const announceTokenVoice = (tokenStr: string, patientName: string, doctorName: string, room: string) => {
    if (!audioEnabled) return;
    playHospitalChime();
    if ('speechSynthesis' in window) {
      setTimeout(() => {
        window.speechSynthesis.cancel();
        const text = `Attention please. Token ${tokenStr}, ${patientName}, please proceed to Room ${room}, ${doctorName}.`;
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.rate = 0.95;
        utterance.pitch = 1.0;
        window.speechSynthesis.speak(utterance);
      }, 350);
    }
  };

  // Algorithm: Calculate patients ahead of a given token
  const getPatientsAhead = (tokenNum: number): number => {
    const target = queue.find(q => q.tokenNumber === tokenNum);
    if (!target || target.status !== 'WAITING') return 0;
    return queue.filter(q => q.status === 'WAITING' && q.score > target.score).length +
      queue.filter(q => q.status === 'WAITING' && q.score === target.score && q.tokenNumber < target.tokenNumber).length;
  };

  // Algorithm: Calculate waiting time (WaitingTimeService.java)
  const calculateWaitTime = (tokenNum: number): number => {
    const target = queue.find(q => q.tokenNumber === tokenNum);
    if (!target) return 0;
    if (target.status === 'CALLED' || target.status === 'IN_CONSULTATION') return 0;
    const ahead = getPatientsAhead(tokenNum);
    const avgConsult = 8; // Dr. Rajesh Kumar average duration
    return ahead * avgConsult + 4; // Including estimated remaining time in cabin
  };

  const currentServingToken = queue.find(q => q.status === 'CALLED' || q.status === 'IN_CONSULTATION')?.tokenNumber || 0;
  const currentServingItem = queue.find(q => q.status === 'CALLED' || q.status === 'IN_CONSULTATION');

  // Doctor Actions
  const handleCallNext = () => {
    const waitingPatients = queue
      .filter(q => q.status === 'WAITING' && q.doctorName === doctorAuth.doctorName)
      .sort((a, b) => b.score - a.score || a.tokenNumber - b.tokenNumber);

    if (waitingPatients.length === 0) {
      showToast("No waiting patients currently in the queue!");
      return;
    }

    const nextPatient = waitingPatients[0];

    // Transition existing CALLED to IN_CONSULTATION or COMPLETED
    setQueue(prev =>
      prev.map(item => {
        if (item.tokenNumber === nextPatient.tokenNumber) {
          return { ...item, status: 'CALLED' };
        }
        if (item.status === 'CALLED') {
          return { ...item, status: 'IN_CONSULTATION' };
        }
        return item;
      })
    );

    setDoctors(prev =>
      prev.map(doc => (doc.id === doctorAuth.doctorId ? { ...doc, status: 'BUSY' } : doc))
    );

    // Announce via Voice & Chime!
    announceTokenVoice(nextPatient.tokenDisplay, nextPatient.patientName, doctorAuth.doctorName, doctorAuth.room);
    showToast(`Calling Token #${nextPatient.tokenDisplay} (${nextPatient.patientName}) to Room ${doctorAuth.room}`);
  };

  const handleStartConsultation = (tokenNum: number) => {
    setQueue(prev =>
      prev.map(item => (item.tokenNumber === tokenNum ? { ...item, status: 'IN_CONSULTATION' } : item))
    );
    showToast(`Started consultation for Token #${tokenNum}`);
  };

  const handleOpenCompleteModal = (item: QueueItem) => {
    setActiveConsultToken(item);
    setDiagnosisText('Essential hypertension stage 1, asymptomatic sinus rhythm.');
    setPrescriptionText('Rx: Tab. Amlodipine 5mg OD x 30 days.\nDiet: Low sodium restriction, daily 30m cardio walk.\nReview: 4 weeks with BP chart.');
    setShowConsultModal(true);
  };

  const handleSaveConsultation = () => {
    if (!activeConsultToken) return;
    setQueue(prev =>
      prev.map(item =>
        item.tokenNumber === activeConsultToken.tokenNumber
          ? { ...item, status: 'COMPLETED' }
          : item
      )
    );
    setShowConsultModal(false);
    setActiveConsultToken(null);
    showToast(`Consultation completed for ${activeConsultToken.tokenDisplay}. Prescription archived.`);
  };

  const handleSkipPatient = (tokenNum: number) => {
    setQueue(prev =>
      prev.map(item => (item.tokenNumber === tokenNum ? { ...item, status: 'SKIPPED' } : item))
    );
    showToast(`Token #${tokenNum} skipped. Marked as absent upon calling.`);
  };

  const handleRecallPatient = (tokenNum: number) => {
    setQueue(prev =>
      prev.map(item => (item.tokenNumber === tokenNum ? { ...item, status: 'WAITING', score: item.score + 10 } : item))
    );
    showToast(`Token #${tokenNum} recalled back to active waiting queue.`);
  };

  // Receptionist Register Walk-in
  const handleRegisterWalkin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!walkinName.trim()) {
      showToast("Please enter patient name.");
      return;
    }

    const nextTokenNum = Math.max(...queue.map(q => q.tokenNumber), 40) + 1;
    const targetDoc = doctors.find(d => d.id === walkinDoctorId) || doctors[0];
    const initialScore = walkinPriority === 'EMERGENCY' ? 300 : walkinPriority === 'HIGH' ? 180 : 100;

    const newItem: QueueItem = {
      queueId: queue.length + 1,
      tokenNumber: nextTokenNum,
      tokenDisplay: `${nextTokenNum}`,
      patientName: walkinName,
      patientUhid: `UHID-2026-${String(nextTokenNum).padStart(4, '0')}`,
      departmentCode: targetDoc.deptCode,
      doctorName: targetDoc.name,
      priority: walkinPriority,
      score: initialScore,
      status: 'WAITING',
      arrivalTime: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      waitMinutes: 20,
      symptoms: 'Walk-in outpatient registration'
    };

    setQueue(prev => [...prev, newItem]);
    setWalkinName('');
    setWalkinPhone('');
    showToast(`Success: Token #${newItem.tokenDisplay} issued for ${newItem.patientName}!`);
  };

  // Publish New Announcement
  const handlePublishAnnouncement = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newNoticeTitle.trim() || !newNoticeMsg.trim()) {
      showToast("Please provide announcement title and details.");
      return;
    }

    const newNotice: HospitalAnnouncement = {
      id: Date.now(),
      title: newNoticeTitle.trim(),
      category: newNoticeCategory,
      message: newNoticeMsg.trim(),
      time: 'Just now',
      author: activePortal === 'ADMIN' ? 'Hospital Administration' : 'Central Reception Desk'
    };

    setAnnouncements(prev => [newNotice, ...prev]);
    setNewNoticeTitle('');
    setNewNoticeMsg('');
    setBroadcastModalOpen(false);
    playHospitalChime();
    showToast("Announcement broadcasted live to all hospital waiting areas!");
  };

  // Handle Patient Login Submit
  const handlePatientLogin = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setCurrentUserRole('PATIENT');
    setActivePortal('PATIENT');
    setIsLoggedIn(true);
    const tokenQuery = patientLoginToken.trim().toUpperCase();
    // Match either number or code like CARD-45 or 45
    const matched = queue.find(
      q => q.tokenDisplay === tokenQuery || String(q.tokenNumber) === tokenQuery || q.patientUhid === patientLoginUhid.trim().toUpperCase()
    );

    if (matched) {
      setPatientAuth({
        isAuthenticated: true,
        tokenNumber: matched.tokenNumber,
        tokenDisplay: matched.tokenDisplay,
        patientName: matched.patientName,
        patientUhid: matched.patientUhid,
        phone: patientLoginPhone || '+1 (555) 234-5678'
      });
      showToast(`Welcome ${matched.patientName}! Viewing Token #${matched.tokenDisplay}.`);
    } else {
      // Create guest session for this token
      const num = parseInt(patientLoginToken.replace(/\D/g, '')) || 45;
      setPatientAuth({
        isAuthenticated: true,
        tokenNumber: num,
        tokenDisplay: `${num}`,
        patientName: 'Registered Patient',
        patientUhid: 'UHID-2026-0045',
        phone: patientLoginPhone
      });
      showToast(`Logged in as Patient for Token #${num}`);
    }
  };

  // Quick 1-Click Patient Login
  const quickLoginPatient = (tokenCode = '45') => {
    const cleanNum = tokenCode.replace(/\D/g, '') || '45';
    const matched = queue.find(q => q.tokenDisplay === tokenCode || String(q.tokenNumber) === cleanNum) || queue[4];
    setPatientLoginToken(matched.tokenDisplay);
    setPatientAuth({
      isAuthenticated: true,
      tokenNumber: matched.tokenNumber,
      tokenDisplay: matched.tokenDisplay,
      patientName: matched.patientName,
      patientUhid: matched.patientUhid,
      phone: '+1 (555) 234-5678'
    });
    setCurrentUserRole('PATIENT');
    setActivePortal('PATIENT');
    setIsLoggedIn(true);
    showToast(`Logged in as Patient: ${matched.patientName} (${matched.tokenDisplay})`);
  };

  // Handle Doctor Login Submit
  const handleDoctorLogin = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setCurrentUserRole('DOCTOR');
    setActivePortal('DOCTOR');
    setIsLoggedIn(true);
    const selectedDoc = doctors.find(d => String(d.id) === docLoginRoom) || doctors[0];
    setDoctorAuth({
      isAuthenticated: true,
      doctorId: selectedDoc.id,
      doctorName: selectedDoc.name,
      dept: selectedDoc.dept,
      room: selectedDoc.room
    });
    showToast(`Authenticated as ${selectedDoc.name} in Room ${selectedDoc.room}`);
  };

  // Quick 1-Click Doctor Login
  const quickLoginDoctor = (doctorId = 1) => {
    const selectedDoc = doctors.find(d => d.id === doctorId) || doctors[0];
    setDocLoginRoom(String(selectedDoc.id));
    setDoctorAuth({
      isAuthenticated: true,
      doctorId: selectedDoc.id,
      doctorName: selectedDoc.name,
      dept: selectedDoc.dept,
      room: selectedDoc.room
    });
    setCurrentUserRole('DOCTOR');
    setActivePortal('DOCTOR');
    setIsLoggedIn(true);
    showToast(`Logged in as Doctor: ${selectedDoc.name} (${selectedDoc.room})`);
  };

  // Handle Receptionist Login Submit
  const handleReceptionistLogin = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setCurrentUserRole('RECEPTIONIST');
    setActivePortal('RECEPTIONIST');
    setIsLoggedIn(true);
    const counterName = recLoginCounter === '1'
      ? 'Counter 1 (Central OPD Registration)'
      : recLoginCounter === '2'
      ? 'Counter 2 (Senior Citizens & Fast Track)'
      : 'Counter 3 (Pediatric & Emergency)';

    setReceptionistAuth({
      isAuthenticated: true,
      staffId: recLoginUser || 'REC-4012',
      staffName: 'Sarah Jenkins',
      counter: counterName
    });
    showToast(`Logged into Reception Desk: ${counterName}`);
  };

  // Quick 1-Click Receptionist Login
  const quickLoginReceptionist = (counter = '1') => {
    setRecLoginCounter(counter);
    const counterName = counter === '1'
      ? 'Counter 1 (Central OPD Registration)'
      : counter === '2'
      ? 'Counter 2 (Senior Citizens & Fast Track)'
      : 'Counter 3 (Pediatric & Emergency)';
    setReceptionistAuth({
      isAuthenticated: true,
      staffId: 'REC-4012',
      staffName: 'Sarah Jenkins',
      counter: counterName
    });
    setCurrentUserRole('RECEPTIONIST');
    setActivePortal('RECEPTIONIST');
    setIsLoggedIn(true);
    showToast(`Logged in as Receptionist: Sarah Jenkins (${counterName})`);
  };

  // Handle Admin Login Submit
  const handleAdminLogin = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setCurrentUserRole('ADMIN');
    setActivePortal('ADMIN');
    setIsLoggedIn(true);
    setAdminAuth({
      isAuthenticated: true,
      username: adminLoginUser || 'admin@hospital.org',
      roleTitle: 'Hospital Operations Director'
    });
    showToast("Authenticated into Hospital Executive Admin Console");
  };

  // Quick 1-Click Admin Login
  const quickLoginAdmin = () => {
    setAdminAuth({
      isAuthenticated: true,
      username: 'admin@hospital.org',
      roleTitle: 'Hospital Operations Director'
    });
    setCurrentUserRole('ADMIN');
    setActivePortal('ADMIN');
    setIsLoggedIn(true);
    showToast("Logged in as Administrator: Full Access Granted");
  };

  // Universal Logout & Return to Four Roles Login Page
  const handleLogout = () => {
    setIsLoggedIn(false);
    setPatientAuth(prev => ({ ...prev, isAuthenticated: false }));
    setDoctorAuth(prev => ({ ...prev, isAuthenticated: false }));
    setReceptionistAuth(prev => ({ ...prev, isAuthenticated: false }));
    setAdminAuth(prev => ({ ...prev, isAuthenticated: false }));
    setActivePortal('PATIENT');
    showToast("Signed out. Select a role to sign in.");
  };

  // Helper: Role-Based Navigation Guard
  const handleSelectPortal = (portal: PortalType) => {
    if (ROLE_PERMISSIONS[currentUserRole].includes(portal)) {
      setActivePortal(portal);
    } else {
      showToast(`Access Denied: ${currentUserRole} cannot access ${portal}. Permitted: ${ROLE_PERMISSIONS[currentUserRole].join(', ')}`);
    }
  };

  const isAuthorized = ROLE_PERMISSIONS[currentUserRole].includes(activePortal);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 flex flex-col font-sans">
      {/* Top Bar Header with Clean Branding & Role-Based Navigation */}
      <header className="bg-slate-900 text-white border-b border-slate-800 px-6 py-3 flex items-center justify-between shadow-md">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-lg bg-blue-600 flex items-center justify-center font-bold text-white shadow-blue-500/30 shadow-lg">
            H+
          </div>
          <div>
            <h1 className="font-bold text-base leading-tight tracking-tight text-white flex items-center gap-2">
              <span>Smart Hospital Queue Management System</span>
            </h1>
          </div>
        </div>

        {/* Navigation & Controls */}
        <div className="flex items-center space-x-1 sm:space-x-2">
          {!isLoggedIn ? (
            <div className="flex items-center space-x-2">
              {activePortal === 'ANNOUNCEMENTS' ? (
                <button
                  onClick={() => setActivePortal('PATIENT')}
                  className="px-3.5 py-1.5 rounded-lg text-xs font-semibold bg-blue-600 hover:bg-blue-500 text-white flex items-center space-x-1.5 transition shadow-xs"
                >
                  <LogIn className="w-3.5 h-3.5" />
                  <span>Back to Sign In</span>
                </button>
              ) : (
                <button
                  onClick={() => setActivePortal('ANNOUNCEMENTS')}
                  className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-slate-800 text-amber-300 hover:bg-slate-700 flex items-center space-x-1.5 transition border border-slate-700"
                >
                  <Megaphone className="w-3.5 h-3.5 text-amber-400" />
                  <span className="hidden sm:inline">Waiting Lounge Display</span>
                  <span className="sm:hidden">Display</span>
                </button>
              )}
            </div>
          ) : (
            <>
              {ROLE_PERMISSIONS[currentUserRole].includes('PATIENT') && (
                <button
                  onClick={() => handleSelectPortal('PATIENT')}
                  className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition ${
                    activePortal === 'PATIENT'
                      ? 'bg-blue-600 text-white shadow-xs'
                      : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                  }`}
                >
                  <User className="w-3.5 h-3.5" />
                  <span>Patient</span>
                </button>
              )}

              {ROLE_PERMISSIONS[currentUserRole].includes('DOCTOR') && (
                <button
                  onClick={() => handleSelectPortal('DOCTOR')}
                  className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition ${
                    activePortal === 'DOCTOR'
                      ? 'bg-indigo-600 text-white shadow-xs'
                      : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                  }`}
                >
                  <Stethoscope className="w-3.5 h-3.5" />
                  <span>Doctor OPD</span>
                </button>
              )}

              {ROLE_PERMISSIONS[currentUserRole].includes('RECEPTIONIST') && (
                <button
                  onClick={() => handleSelectPortal('RECEPTIONIST')}
                  className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition ${
                    activePortal === 'RECEPTIONIST'
                      ? 'bg-teal-600 text-white shadow-xs'
                      : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                  }`}
                >
                  <Building2 className="w-3.5 h-3.5" />
                  <span>Reception</span>
                </button>
              )}

              {ROLE_PERMISSIONS[currentUserRole].includes('ADMIN') && (
                <button
                  onClick={() => handleSelectPortal('ADMIN')}
                  className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition ${
                    activePortal === 'ADMIN'
                      ? 'bg-purple-600 text-white shadow-xs'
                      : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                  }`}
                >
                  <Shield className="w-3.5 h-3.5" />
                  <span>Admin</span>
                </button>
              )}

              {ROLE_PERMISSIONS[currentUserRole].includes('ANNOUNCEMENTS') && (
                <button
                  onClick={() => handleSelectPortal('ANNOUNCEMENTS')}
                  className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition ${
                    activePortal === 'ANNOUNCEMENTS'
                      ? 'bg-amber-500 text-slate-950 font-bold shadow-xs'
                      : 'bg-slate-800 text-amber-300 hover:bg-slate-700'
                  }`}
                >
                  <Megaphone className="w-3.5 h-3.5" />
                  <span>Announcements</span>
                </button>
              )}

              {/* Header Sign Out Button */}
              <div className="pl-2 border-l border-slate-800 ml-1">
                <button
                  onClick={handleLogout}
                  className="px-2.5 py-1.5 rounded-lg text-xs font-semibold bg-rose-600/90 hover:bg-rose-600 text-white flex items-center space-x-1.5 transition shadow-xs"
                  title="Sign out"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  <span className="hidden sm:inline">Sign Out</span>
                </button>
              </div>
            </>
          )}
        </div>
      </header>

      {/* Sub-header Context Bar */}
      <div className="bg-white border-b border-slate-200 px-6 py-2 flex items-center justify-between text-xs text-slate-600 shadow-xs">
        <div className="flex items-center space-x-3">
          <span className="font-medium text-slate-700">
            {!isLoggedIn && activePortal !== 'ANNOUNCEMENTS' && 'Sign In'}
            {!isLoggedIn && activePortal === 'ANNOUNCEMENTS' && 'Waiting Lounge Display'}
            {isLoggedIn && activePortal === 'PATIENT' && 'Patient Outpatient Queue'}
            {isLoggedIn && activePortal === 'DOCTOR' && `${doctorAuth.doctorName} • Room ${doctorAuth.room}`}
            {isLoggedIn && activePortal === 'RECEPTIONIST' && `Registration • ${receptionistAuth.counter}`}
            {isLoggedIn && activePortal === 'ADMIN' && 'Hospital Queue Overview'}
            {isLoggedIn && activePortal === 'ANNOUNCEMENTS' && 'Waiting Lounge Display'}
          </span>

          {isLoggedIn && (
            <span className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${
              currentUserRole === 'PATIENT' ? 'bg-blue-50 text-blue-700' :
              currentUserRole === 'DOCTOR' ? 'bg-indigo-50 text-indigo-700' :
              currentUserRole === 'RECEPTIONIST' ? 'bg-teal-50 text-teal-700' :
              'bg-purple-50 text-purple-700'
            }`}>
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
              {currentUserRole}
            </span>
          )}
        </div>

        <div className="flex items-center space-x-2 text-slate-400 font-mono text-[11px]">
          <span>{currentTime}</span>
        </div>
      </div>

      {/* Main Container */}
      <main className="flex-1 flex flex-col p-6 max-w-7xl w-full mx-auto">

        {/* When not logged in, first open the Login Page with four roles */}
        {!isLoggedIn && activePortal !== 'ANNOUNCEMENTS' ? (
          <RoleLoginPage
            selectedRole={loginSelectedRole}
            onSelectRole={setLoginSelectedRole}
            patientToken={patientLoginToken}
            setPatientToken={setPatientLoginToken}
            patientPhone={patientLoginPhone}
            setPatientPhone={setPatientLoginPhone}
            patientTab={patientLoginTab}
            setPatientTab={setPatientLoginTab}
            patientUhid={patientLoginUhid}
            setPatientUhid={setPatientLoginUhid}
            onPatientSubmit={handlePatientLogin}
            onQuickPatientLogin={quickLoginPatient}
            doctorUser={docLoginUser}
            setDoctorUser={setDocLoginUser}
            doctorPass={docLoginPass}
            setDoctorPass={setDocLoginPass}
            doctorRoom={docLoginRoom}
            setDoctorRoom={setDocLoginRoom}
            doctors={doctors}
            onDoctorSubmit={handleDoctorLogin}
            onQuickDoctorLogin={quickLoginDoctor}
            receptionUser={recLoginUser}
            setReceptionUser={setRecLoginUser}
            receptionPass={recLoginPass}
            setReceptionPass={setRecLoginPass}
            receptionCounter={recLoginCounter}
            setReceptionCounter={setRecLoginCounter}
            onReceptionSubmit={handleReceptionistLogin}
            onQuickReceptionLogin={quickLoginReceptionist}
            adminUser={adminLoginUser}
            setAdminUser={setAdminLoginUser}
            adminPass={adminLoginPass}
            setAdminPass={setAdminLoginPass}
            onAdminSubmit={handleAdminLogin}
            onQuickAdminLogin={quickLoginAdmin}
            onOpenAnnouncements={() => setActivePortal('ANNOUNCEMENTS')}
          />
        ) : !isAuthorized ? (
          <div className="max-w-md w-full mx-auto my-auto space-y-4 text-center">
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-8 space-y-4">
              <div className="w-12 h-12 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center mx-auto">
                <ShieldAlert className="w-6 h-6" />
              </div>
              <h2 className="text-lg font-bold text-slate-900">Access Restricted</h2>
              <p className="text-xs text-slate-500 leading-relaxed">
                Your role ({currentUserRole}) does not have permission to view this section.
              </p>
              <div className="pt-2 flex justify-center gap-3">
                <button
                  onClick={() => {
                    if (currentUserRole === 'PATIENT') setActivePortal('PATIENT');
                    else if (currentUserRole === 'DOCTOR') setActivePortal('DOCTOR');
                    else if (currentUserRole === 'RECEPTIONIST') setActivePortal('RECEPTIONIST');
                    else setActivePortal('ADMIN');
                  }}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg text-xs transition"
                >
                  Return to Dashboard
                </button>
                <button
                  onClick={handleLogout}
                  className="px-4 py-2 border border-slate-200 hover:bg-slate-50 text-slate-700 font-medium rounded-lg text-xs transition"
                >
                  Sign Out
                </button>
              </div>
            </div>
          </div>
        ) : (
          <>
            {/* ========================================================= */}
            {/* 1. PATIENT PORTAL (SEPARATE LOGIN PAGE & DASHBOARD)       */}
            {/* ========================================================= */}
            {activePortal === 'PATIENT' && (
          !patientAuth.isAuthenticated ? (
            /* DEDICATED PATIENT LOGIN PAGE */
            <div className="max-w-md w-full mx-auto my-auto space-y-6">
              <div className="bg-white rounded-2xl border border-slate-200 shadow-xl p-8">
                <div className="text-center mb-6">
                  <div className="w-14 h-14 bg-blue-100 text-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-3 shadow-inner">
                    <User className="w-7 h-7" />
                  </div>
                  <h2 className="text-2xl font-extrabold text-slate-900">Patient Queue Login</h2>
                  <p className="text-xs text-slate-500 mt-1">
                    Track your daily token number, position in line, and estimated consultation time.
                  </p>
                </div>

                {/* Login Tabs: Token vs UHID */}
                <div className="flex rounded-lg bg-slate-100 p-1 mb-6">
                  <button
                    type="button"
                    onClick={() => setPatientLoginTab('TOKEN')}
                    className={`flex-1 py-1.5 text-xs font-bold rounded-md transition ${
                      patientLoginTab === 'TOKEN' ? 'bg-white text-blue-600 shadow-xs' : 'text-slate-500 hover:text-slate-900'
                    }`}
                  >
                    Token &amp; Mobile No.
                  </button>
                  <button
                    type="button"
                    onClick={() => setPatientLoginTab('UHID')}
                    className={`flex-1 py-1.5 text-xs font-bold rounded-md transition ${
                      patientLoginTab === 'UHID' ? 'bg-white text-blue-600 shadow-xs' : 'text-slate-500 hover:text-slate-900'
                    }`}
                  >
                    Hospital UHID / MRN
                  </button>
                </div>

                <form onSubmit={handlePatientLogin} className="space-y-4">
                  {patientLoginTab === 'TOKEN' ? (
                    <>
                      <div>
                        <label className="block text-xs font-semibold text-slate-700 mb-1">
                          Today&apos;s Token Number *
                        </label>
                        <input
                          type="text"
                          value={patientLoginToken}
                          onChange={e => setPatientLoginToken(e.target.value)}
                          placeholder="e.g. 45"
                          required
                          className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 font-mono"
                        />
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-slate-700 mb-1">
                          Registered Mobile Number *
                        </label>
                        <input
                          type="tel"
                          value={patientLoginPhone}
                          onChange={e => setPatientLoginPhone(e.target.value)}
                          placeholder="e.g. +1 (555) 234-5678"
                          required
                          className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                        />
                      </div>
                    </>
                  ) : (
                    <>
                      <div>
                        <label className="block text-xs font-semibold text-slate-700 mb-1">
                          Patient UHID / Medical Record Number *
                        </label>
                        <input
                          type="text"
                          value={patientLoginUhid}
                          onChange={e => setPatientLoginUhid(e.target.value)}
                          placeholder="e.g. UHID-2026-0045"
                          required
                          className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 font-mono"
                        />
                      </div>
                    </>
                  )}

                  <button
                    type="submit"
                    className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                  >
                    <LogIn className="w-4 h-4" />
                    <span>Track My Token Live</span>
                  </button>
                </form>

                {/* Instant 1-Click Demo Login Presets */}
                <div className="mt-6 pt-5 border-t border-slate-100">
                  <div className="text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-2">
                    Quick 1-Click Patient Presets:
                  </div>
                  <div className="space-y-1.5">
                    <button
                      type="button"
                      onClick={() => {
                        setPatientLoginToken('45');
                        setPatientLoginPhone('+1 (555) 234-5678');
                        setCurrentUserRole('PATIENT');
                        setActivePortal('PATIENT');
                        setPatientAuth({
                          isAuthenticated: true,
                          tokenNumber: 45,
                          tokenDisplay: '45',
                          patientName: 'Robert Chang',
                          patientUhid: 'UHID-2026-0045',
                          phone: '+1 (555) 234-5678'
                        });
                        showToast("Logged in as Patient: Robert Chang (Token #45)!");
                      }}
                      className="w-full text-left px-3 py-2 bg-slate-50 hover:bg-blue-50 hover:text-blue-700 border border-slate-200 rounded-lg text-xs flex justify-between items-center transition"
                    >
                      <span className="font-medium">Robert Chang (Token #45 &bull; Cardiology)</span>
                      <span className="text-[10px] font-mono text-slate-400">Position #3</span>
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setPatientLoginToken('43');
                        setPatientLoginPhone('+1 (555) 345-6789');
                        setCurrentUserRole('PATIENT');
                        setActivePortal('PATIENT');
                        setPatientAuth({
                          isAuthenticated: true,
                          tokenNumber: 43,
                          tokenDisplay: '43',
                          patientName: 'David Kim',
                          patientUhid: 'UHID-2026-0043',
                          phone: '+1 (555) 345-6789'
                        });
                        showToast("Logged in as Patient: David Kim (Token #43)!");
                      }}
                      className="w-full text-left px-3 py-2 bg-slate-50 hover:bg-blue-50 hover:text-blue-700 border border-slate-200 rounded-lg text-xs flex justify-between items-center transition"
                    >
                      <span className="font-medium">David Kim (Token #43 &bull; Next Up)</span>
                      <span className="text-[10px] font-mono text-slate-400">Position #1</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            /* PATIENT LIVE TOKEN DASHBOARD */
            <div className="space-y-6">
              {/* Patient Session Bar */}
              <div className="bg-white rounded-xl border border-slate-200 p-4 flex items-center justify-between shadow-xs">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold">
                    {patientAuth.patientName.charAt(0)}
                  </div>
                  <div>
                    <div className="font-bold text-slate-900 text-sm">{patientAuth.patientName}</div>
                    <div className="text-xs text-slate-500">
                      Token <strong className="text-blue-600 font-mono">#{patientAuth.tokenDisplay}</strong> &bull; {patientAuth.patientUhid} &bull; {patientAuth.phone}
                    </div>
                  </div>
                </div>

                <button
                  onClick={handleLogout}
                  className="px-3 py-1.5 text-xs font-semibold text-rose-600 hover:bg-rose-50 border border-rose-200 rounded-lg flex items-center space-x-1.5 transition"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  <span>Change Token / Sign Out</span>
                </button>
              </div>


              {/* Token Called Alert */}
              {currentServingToken === patientAuth.tokenNumber && (
                <div className="bg-amber-100 border-2 border-amber-400 rounded-xl p-5 shadow-lg flex items-center justify-between animate-bounce">
                  <div>
                    <h2 className="text-xl font-extrabold text-amber-900">IT IS YOUR TURN! PROCEED TO ROOM OPD-204</h2>
                    <p className="text-sm text-amber-800">Doctor Dr. Rajesh Kumar is ready to see you.</p>
                  </div>
                  <span className="px-4 py-1.5 bg-amber-500 text-white font-bold rounded-lg text-sm shadow-sm">NOW CALLING</span>
                </div>
              )}

              {/* Hero Token Board */}
              <div className="bg-gradient-to-r from-blue-900 via-blue-800 to-indigo-800 text-white rounded-2xl p-8 shadow-xl relative overflow-hidden">
                <div className="flex justify-between items-start mb-6">
                  <div>
                    <h2 className="text-2xl font-bold">Live Outpatient Queue &bull; Cardiology</h2>
                    <p className="text-blue-200 text-sm">Dr. Rajesh Kumar &bull; Room OPD-204 &bull; Main Cardio Pavilion</p>
                  </div>
                  <span className="px-3 py-1 bg-blue-700/60 border border-blue-400/30 rounded-full text-xs font-semibold uppercase tracking-wider text-blue-100">
                    {currentServingToken === patientAuth.tokenNumber ? 'Called Now' : 'Waiting in Queue'}
                  </span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-4 gap-6 text-center">
                  <div className="bg-white/10 backdrop-blur-xs p-5 rounded-xl border border-white/10">
                    <div className="text-blue-200 text-xs uppercase font-medium">Your Token</div>
                    <div className="text-5xl font-black tracking-tight mt-1 font-mono text-white">
                      {patientAuth.tokenDisplay}
                    </div>
                    <div className="text-xs text-blue-200 mt-2 font-mono">
                      Priority: {queue.find(q => q.tokenNumber === patientAuth.tokenNumber)?.priority || 'NORMAL'}
                    </div>
                  </div>

                  <div className="bg-white/10 backdrop-blur-xs p-5 rounded-xl border border-white/10">
                    <div className="text-blue-200 text-xs uppercase font-medium">Currently Serving</div>
                    <div className="text-5xl font-black tracking-tight mt-1 font-mono text-amber-300">
                      {currentServingItem ? currentServingItem.tokenDisplay : '--'}
                    </div>
                    <div className="text-xs text-blue-200 mt-2">
                      {currentServingItem ? currentServingItem.patientName : 'Doctor Cabin Available'}
                    </div>
                  </div>

                  <div className="bg-white/10 backdrop-blur-xs p-5 rounded-xl border border-white/10">
                    <div className="text-blue-200 text-xs uppercase font-medium">Patients Ahead</div>
                    <div className="text-5xl font-black tracking-tight mt-1 font-mono text-white">
                      {getPatientsAhead(patientAuth.tokenNumber)}
                    </div>
                    <div className="text-xs text-blue-200 mt-2">In line before you</div>
                  </div>

                  <div className="bg-white/10 backdrop-blur-xs p-5 rounded-xl border border-white/10">
                    <div className="text-blue-200 text-xs uppercase font-medium">Estimated Wait Time</div>
                    <div className="text-5xl font-black tracking-tight mt-1 font-mono text-emerald-300">
                      ~{calculateWaitTime(patientAuth.tokenNumber)}m
                    </div>
                    <div className="text-xs text-emerald-200 mt-2">
                      Based on current pace
                    </div>
                  </div>
                </div>
              </div>

              {/* Live Queue Table */}
              <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-xs">
                <div className="px-6 py-4 border-b border-slate-200 flex justify-between items-center bg-slate-50/50">
                  <h3 className="font-bold text-slate-800 text-sm">Active Queue</h3>
                  <span className="text-xs text-slate-500">Live order</span>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-xs">
                    <thead className="bg-slate-100 text-slate-600 font-bold border-b border-slate-200 uppercase tracking-wider">
                      <tr>
                        <th className="py-3 px-4">Token</th>
                        <th className="py-3 px-4">Patient Name</th>
                        <th className="py-3 px-4">Priority</th>
                        <th className="py-3 px-4">Arrival</th>
                        <th className="py-3 px-4">Status</th>
                        <th className="py-3 px-4">Est. Wait</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {queue.map(item => (
                        <tr
                          key={item.tokenNumber}
                          className={`hover:bg-slate-50 transition ${
                            item.tokenNumber === patientAuth.tokenNumber ? 'bg-blue-50/70 font-bold border-l-4 border-l-blue-600' : ''
                          }`}
                        >
                          <td className="py-3 px-4 font-mono font-bold text-slate-900">
                            {item.tokenDisplay}
                            {item.tokenNumber === patientAuth.tokenNumber && (
                              <span className="ml-1.5 px-1.5 py-0.5 bg-blue-600 text-white rounded text-[10px] font-sans font-bold">YOU</span>
                            )}
                          </td>
                          <td className="py-3 px-4">{item.patientName}</td>
                          <td className="py-3 px-4">
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                              item.priority === 'EMERGENCY' ? 'bg-rose-100 text-rose-800' :
                              item.priority === 'HIGH' ? 'bg-amber-100 text-amber-800' : 'bg-slate-100 text-slate-700'
                            }`}>
                              {item.priority}
                            </span>
                          </td>
                          <td className="py-3 px-4 text-slate-500">{item.arrivalTime}</td>
                          <td className="py-3 px-4">
                            <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                              item.status === 'CALLED' ? 'bg-amber-100 text-amber-800 animate-pulse' :
                              item.status === 'IN_CONSULTATION' ? 'bg-blue-100 text-blue-800' :
                              item.status === 'COMPLETED' ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-100 text-slate-600'
                            }`}>
                              {item.status}
                            </span>
                          </td>
                          <td className="py-3 px-4 font-mono font-semibold text-slate-700">
                            {item.status === 'COMPLETED' ? 'Done' :
                             item.status === 'CALLED' || item.status === 'IN_CONSULTATION' ? '0 min (Inside)' :
                             `~${calculateWaitTime(item.tokenNumber)} min`}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )
        )}

        {/* ========================================================= */}
        {/* 2. DOCTOR OPD CABIN (SEPARATE LOGIN PAGE & CONSOLE)       */}
        {/* ========================================================= */}
        {activePortal === 'DOCTOR' && (
          !doctorAuth.isAuthenticated ? (
            /* DEDICATED DOCTOR LOGIN PAGE */
            <div className="max-w-md w-full mx-auto my-auto space-y-6">
              <div className="bg-white rounded-2xl border border-slate-200 shadow-xl p-8">
                <div className="text-center mb-6">
                  <div className="w-14 h-14 bg-indigo-100 text-indigo-700 rounded-2xl flex items-center justify-center mx-auto mb-3 shadow-inner">
                    <Stethoscope className="w-7 h-7" />
                  </div>
                  <h2 className="text-2xl font-extrabold text-slate-900">Doctor OPD Login</h2>
                  <p className="text-xs text-slate-500 mt-1">
                    Clinical consultation console &bull; Electronic Prescriptions &bull; Queue Calling
                  </p>
                </div>

                <form onSubmit={handleDoctorLogin} className="space-y-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Doctor Medical ID / Username *
                    </label>
                    <input
                      type="text"
                      value={docLoginUser}
                      onChange={e => setDocLoginUser(e.target.value)}
                      placeholder="e.g. dr.kumar@hospital.org"
                      required
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Clinical Password *
                    </label>
                    <input
                      type="password"
                      value={docLoginPass}
                      onChange={e => setDocLoginPass(e.target.value)}
                      required
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 font-mono"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      OPD Cabin Room &amp; Specialty *
                    </label>
                    <select
                      value={docLoginRoom}
                      onChange={e => setDocLoginRoom(e.target.value)}
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
                    >
                      {doctors.map(d => (
                        <option key={d.id} value={d.id}>
                          {d.room} &bull; {d.name} ({d.dept})
                        </option>
                      ))}
                    </select>
                  </div>

                  <button
                    type="submit"
                    className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                  >
                    <LogIn className="w-4 h-4" />
                    <span>Sign In to OPD Cabin</span>
                  </button>
                </form>

                <div className="mt-6 pt-5 border-t border-slate-100">
                  <div className="text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-2">
                    Quick Clinical Presets:
                  </div>
                  <button
                    type="button"
                    onClick={() => {
                      setDocLoginRoom('1');
                      setDocLoginUser('dr.kumar@hospital.org');
                      setCurrentUserRole('DOCTOR');
                      setActivePortal('DOCTOR');
                      setDoctorAuth({
                        isAuthenticated: true,
                        doctorId: 1,
                        doctorName: 'Dr. Rajesh Kumar',
                        dept: 'Cardiology',
                        room: 'OPD-204'
                      });
                      showToast("Logged in as Doctor: Dr. Rajesh Kumar (OPD-204 Cardiology)!");
                    }}
                    className="w-full text-left px-3 py-2 bg-slate-50 hover:bg-indigo-50 hover:text-indigo-700 border border-slate-200 rounded-lg text-xs flex justify-between items-center transition"
                  >
                    <span className="font-medium">Dr. Rajesh Kumar &bull; OPD-204 (Cardiology)</span>
                    <span className="text-[10px] text-indigo-600 font-bold">Select &rarr;</span>
                  </button>
                </div>
              </div>
            </div>
          ) : (
            /* DOCTOR CONSULTATION DASHBOARD */
            <div className="space-y-6">
              {/* Doctor Header Bar */}
              <div className="bg-white rounded-xl border border-slate-200 p-4 flex items-center justify-between shadow-xs">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 rounded-full bg-indigo-100 text-indigo-700 flex items-center justify-center font-bold">
                    <Stethoscope className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="font-bold text-slate-900 text-sm">
                      {doctorAuth.doctorName} &bull; Room {doctorAuth.room} ({doctorAuth.dept})
                    </div>
                    <div className="text-xs text-slate-500">
                      Standard consultation benchmark: <strong>8 minutes / patient</strong> &bull; Status: 
                      <span className="ml-1 text-emerald-600 font-bold">Active Shift</span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center space-x-2">
                  <button
                    onClick={handleLogout}
                    className="px-3 py-1.5 text-xs font-semibold text-rose-600 hover:bg-rose-50 border border-rose-200 rounded-lg flex items-center space-x-1.5 transition"
                  >
                    <LogOut className="w-3.5 h-3.5" />
                    <span>Doctor Sign Out</span>
                  </button>
                </div>
              </div>

              {/* Consultation Control Deck */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Active Patient Card */}
                <div className="md:col-span-2 bg-white rounded-xl border border-slate-200 p-6 shadow-xs flex flex-col justify-between">
                  <div>
                    <div className="flex justify-between items-start mb-4">
                      <div>
                        <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Patient In Cabin</span>
                        <h3 className="text-xl font-bold text-slate-900 mt-0.5">
                          {currentServingItem ? currentServingItem.patientName : 'No Active Patient in Cabin'}
                        </h3>
                      </div>
                      {currentServingItem && (
                        <span className="px-3 py-1 bg-amber-100 text-amber-800 font-bold rounded-lg text-xs">
                          TOKEN #{currentServingItem.tokenDisplay}
                        </span>
                      )}
                    </div>

                    {currentServingItem ? (
                      <div className="bg-slate-50 rounded-xl p-4 border border-slate-100 space-y-2 text-xs">
                        <div className="grid grid-cols-2 gap-2">
                          <div>
                            <span className="text-slate-400">UHID:</span>
                            <span className="font-mono ml-1 text-slate-800">{currentServingItem.patientUhid}</span>
                          </div>
                          <div>
                            <span className="text-slate-400">Arrival Time:</span>
                            <span className="ml-1 text-slate-800">{currentServingItem.arrivalTime}</span>
                          </div>
                          <div>
                            <span className="text-slate-400">Priority:</span>
                            <span className="font-bold ml-1 text-amber-700">{currentServingItem.priority} ({currentServingItem.score} pts)</span>
                          </div>
                          <div>
                            <span className="text-slate-400">Status:</span>
                            <span className="font-bold ml-1 text-blue-700">{currentServingItem.status}</span>
                          </div>
                        </div>
                        <div className="pt-2 border-t border-slate-200">
                          <span className="text-slate-400">Chief Complaint / Triage:</span>
                          <p className="text-slate-700 font-medium mt-0.5">{currentServingItem.symptoms}</p>
                        </div>
                      </div>
                    ) : (
                      <div className="text-center py-8 text-slate-400 text-xs">
                        Doctor cabin is ready. Click &ldquo;Call Next Patient&rdquo; to pull the highest priority token.
                      </div>
                    )}
                  </div>

                  {/* Actions Bar */}
                  <div className="pt-6 border-t border-slate-100 mt-6 flex flex-wrap gap-2">
                    <button
                      onClick={handleCallNext}
                      className="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-lg shadow-sm text-xs flex items-center space-x-2 transition"
                    >
                      <Play className="w-4 h-4 fill-white" />
                      <span>Call Next Patient</span>
                    </button>

                    {currentServingItem && currentServingItem.status === 'CALLED' && (
                      <button
                        onClick={() => handleStartConsultation(currentServingItem.tokenNumber)}
                        className="px-4 py-2.5 bg-amber-500 hover:bg-amber-600 text-white font-bold rounded-lg shadow-sm text-xs flex items-center space-x-1.5 transition"
                      >
                        <UserCheck className="w-4 h-4" />
                        <span>Start Consultation</span>
                      </button>
                    )}

                    {currentServingItem && (
                      <button
                        onClick={() => handleOpenCompleteModal(currentServingItem)}
                        className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-lg shadow-sm text-xs flex items-center space-x-1.5 transition"
                      >
                        <CheckCircle2 className="w-4 h-4" />
                        <span>Complete &amp; Prescribe</span>
                      </button>
                    )}

                    {currentServingItem && (
                      <button
                        onClick={() => handleSkipPatient(currentServingItem.tokenNumber)}
                        className="px-3 py-2 border border-slate-300 hover:bg-slate-50 text-slate-600 font-semibold rounded-lg text-xs flex items-center space-x-1 transition"
                      >
                        <SkipForward className="w-3.5 h-3.5" />
                        <span>Skip Patient</span>
                      </button>
                    )}
                  </div>
                </div>

                {/* Queue Summary Counter Card */}
                <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-xs flex flex-col justify-between">
                  <div>
                    <h3 className="text-base font-bold text-slate-900 mb-2">Queue Summary</h3>
                    <p className="text-xs text-slate-500 mb-4">Patient status overview for your cabin</p>

                    <div className="space-y-2">
                      <div className="p-3 bg-blue-50 rounded-lg flex justify-between items-center text-xs">
                        <span className="font-semibold text-blue-900">Waiting:</span>
                        <span className="font-mono font-bold text-blue-700 text-base">
                          {queue.filter(q => q.status === 'WAITING' && q.doctorName === doctorAuth.doctorName).length}
                        </span>
                      </div>
                      <div className="p-3 bg-emerald-50 rounded-lg flex justify-between items-center text-xs">
                        <span className="font-semibold text-emerald-900">Completed:</span>
                        <span className="font-mono font-bold text-emerald-700 text-base">
                          {queue.filter(q => q.status === 'COMPLETED' && q.doctorName === doctorAuth.doctorName).length}
                        </span>
                      </div>
                      <div className="p-3 bg-amber-50 rounded-lg flex justify-between items-center text-xs">
                        <span className="font-semibold text-amber-900">Skipped:</span>
                        <span className="font-mono font-bold text-amber-700 text-base">
                          {queue.filter(q => q.status === 'SKIPPED' && q.doctorName === doctorAuth.doctorName).length}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="mt-4 pt-4 border-t border-slate-100 text-[11px] text-slate-400">
                    Next in line: <strong className="text-slate-700">
                      {queue.find(q => q.status === 'WAITING' && q.doctorName === doctorAuth.doctorName)?.patientName || 'None'}
                    </strong>
                  </div>
                </div>
              </div>

              {/* Waiting Room Roster */}
              <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-xs">
                <div className="px-6 py-4 border-b border-slate-200 bg-slate-50 flex justify-between items-center">
                  <h3 className="font-bold text-slate-800 text-sm">Waiting Patients</h3>
                  <span className="text-xs text-slate-500">Live order</span>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-xs">
                    <thead className="bg-slate-100 text-slate-600 font-bold border-b border-slate-200 uppercase">
                      <tr>
                        <th className="py-2.5 px-4">Token</th>
                        <th className="py-2.5 px-4">Patient</th>
                        <th className="py-2.5 px-4">Triage Priority</th>
                        <th className="py-2.5 px-4">Symptoms</th>
                        <th className="py-2.5 px-4">Arrival</th>
                        <th className="py-2.5 px-4">Status</th>
                        <th className="py-2.5 px-4 text-right">Actions</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {queue.filter(q => q.doctorName === doctorAuth.doctorName).map(item => (
                        <tr key={item.tokenNumber} className="hover:bg-slate-50">
                          <td className="py-2.5 px-4 font-mono font-bold">{item.tokenDisplay}</td>
                          <td className="py-2.5 px-4">
                            <div className="font-semibold text-slate-800">{item.patientName}</div>
                            <div className="text-[10px] text-slate-400 font-mono">{item.patientUhid}</div>
                          </td>
                          <td className="py-2.5 px-4">
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                              item.priority === 'EMERGENCY' ? 'bg-rose-100 text-rose-800' :
                              item.priority === 'HIGH' ? 'bg-amber-100 text-amber-800' : 'bg-slate-100 text-slate-700'
                            }`}>
                              {item.priority}
                            </span>
                          </td>
                          <td className="py-2.5 px-4 text-slate-600 max-w-xs truncate">{item.symptoms}</td>
                          <td className="py-2.5 px-4 text-slate-500">{item.arrivalTime}</td>
                          <td className="py-2.5 px-4">
                            <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                              item.status === 'CALLED' ? 'bg-amber-100 text-amber-800' :
                              item.status === 'IN_CONSULTATION' ? 'bg-blue-100 text-blue-800' :
                              item.status === 'COMPLETED' ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-100 text-slate-600'
                            }`}>
                              {item.status}
                            </span>
                          </td>
                          <td className="py-2.5 px-4 text-right">
                            {item.status === 'SKIPPED' && (
                              <button
                                onClick={() => handleRecallPatient(item.tokenNumber)}
                                className="px-2 py-1 bg-amber-50 text-amber-700 border border-amber-200 rounded text-[11px] font-bold hover:bg-amber-100"
                              >
                                Recall
                              </button>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )
        )}

        {/* ========================================================= */}
        {/* 3. RECEPTIONIST DESK (SEPARATE LOGIN PAGE & REGISTRATION) */}
        {/* ========================================================= */}
        {activePortal === 'RECEPTIONIST' && (
          !receptionistAuth.isAuthenticated ? (
            /* DEDICATED RECEPTIONIST LOGIN PAGE */
            <div className="max-w-md w-full mx-auto my-auto space-y-6">
              <div className="bg-white rounded-2xl border border-slate-200 shadow-xl p-8">
                <div className="text-center mb-6">
                  <div className="w-14 h-14 bg-teal-100 text-teal-700 rounded-2xl flex items-center justify-center mx-auto mb-3 shadow-inner">
                    <Building2 className="w-7 h-7" />
                  </div>
                  <h2 className="text-2xl font-extrabold text-slate-900">Receptionist Desk Login</h2>
                  <p className="text-xs text-slate-500 mt-1">
                    Front-desk walk-in registration, triage assessment &amp; token dispenser
                  </p>
                </div>

                <form onSubmit={handleReceptionistLogin} className="space-y-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Staff ID / Username *
                    </label>
                    <input
                      type="text"
                      value={recLoginUser}
                      onChange={e => setRecLoginUser(e.target.value)}
                      placeholder="e.g. REC-4012 or receptionist"
                      required
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-teal-500 focus:ring-2 focus:ring-teal-100"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Staff Password *
                    </label>
                    <input
                      type="password"
                      value={recLoginPass}
                      onChange={e => setRecLoginPass(e.target.value)}
                      required
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-teal-500 focus:ring-2 focus:ring-teal-100 font-mono"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Registration Counter *
                    </label>
                    <select
                      value={recLoginCounter}
                      onChange={e => setRecLoginCounter(e.target.value)}
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-teal-500 focus:ring-2 focus:ring-teal-100"
                    >
                      <option value="1">Counter 1 - Central OPD Registration &amp; Triage</option>
                      <option value="2">Counter 2 - Senior Citizens &amp; Fast Track</option>
                      <option value="3">Counter 3 - Pediatric &amp; Emergency Counter</option>
                    </select>
                  </div>

                  <button
                    type="submit"
                    className="w-full py-3 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                  >
                    <LogIn className="w-4 h-4" />
                    <span>Open Registration Desk</span>
                  </button>
                </form>

                <div className="mt-6 pt-5 border-t border-slate-100">
                  <button
                    type="button"
                    onClick={() => {
                      setRecLoginCounter('1');
                      setRecLoginUser('REC-4012');
                      setCurrentUserRole('RECEPTIONIST');
                      setActivePortal('RECEPTIONIST');
                      setReceptionistAuth({
                        isAuthenticated: true,
                        staffId: 'REC-4012',
                        staffName: 'Sarah Jenkins',
                        counter: 'Counter 1 (Central OPD Registration)'
                      });
                      showToast("Logged in as Receptionist: Sarah Jenkins at Counter 1!");
                    }}
                    className="w-full text-left px-3 py-2 bg-slate-50 hover:bg-teal-50 hover:text-teal-700 border border-slate-200 rounded-lg text-xs flex justify-between items-center transition"
                  >
                    <span className="font-medium">Counter 1 Desk &bull; Sarah Jenkins</span>
                    <span className="text-[10px] text-teal-600 font-bold">1-Click Login &rarr;</span>
                  </button>
                </div>
              </div>
            </div>
          ) : (
            /* RECEPTIONIST DASHBOARD */
            <div className="space-y-6">
              {/* Reception Header Bar */}
              <div className="bg-white rounded-xl border border-slate-200 p-4 flex items-center justify-between shadow-xs">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 rounded-full bg-teal-100 text-teal-700 flex items-center justify-center font-bold">
                    <Building2 className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="font-bold text-slate-900 text-sm">
                      Front Desk Staff: {receptionistAuth.staffName} ({receptionistAuth.staffId})
                    </div>
                    <div className="text-xs text-slate-500">
                      Active Counter: <strong className="text-teal-700">{receptionistAuth.counter}</strong>
                    </div>
                  </div>
                </div>

                <button
                  onClick={handleLogout}
                  className="px-3 py-1.5 text-xs font-semibold text-rose-600 hover:bg-rose-50 border border-rose-200 rounded-lg flex items-center space-x-1.5 transition"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  <span>Desk Sign Out</span>
                </button>
              </div>

              {/* Registration Form & Status Grid */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Left: Walk-in Registration Form */}
                <div className="md:col-span-2 bg-white rounded-xl border border-slate-200 p-6 shadow-xs">
                  <h3 className="text-base font-bold text-slate-900 mb-1">Issue New Patient Token (Walk-In &amp; Triage)</h3>
                  <p className="text-xs text-slate-500 mb-4">Assign department and clinical priority to compute queue score.</p>

                  <form onSubmit={handleRegisterWalkin} className="space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-xs font-semibold text-slate-700 mb-1">Patient Full Name *</label>
                        <input
                          type="text"
                          value={walkinName}
                          onChange={e => setWalkinName(e.target.value)}
                          placeholder="e.g. Samuel Henderson"
                          required
                          className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                        />
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-slate-700 mb-1">Contact Phone *</label>
                        <input
                          type="tel"
                          value={walkinPhone}
                          onChange={e => setWalkinPhone(e.target.value)}
                          placeholder="e.g. +1 555-0192"
                          className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                        />
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-xs font-semibold text-slate-700 mb-1">Doctor &amp; Department</label>
                        <select
                          value={walkinDoctorId}
                          onChange={e => setWalkinDoctorId(Number(e.target.value))}
                          className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                        >
                          {doctors.map(doc => (
                            <option key={doc.id} value={doc.id}>
                              {doc.name} - {doc.dept} ({doc.room})
                            </option>
                          ))}
                        </select>
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-slate-700 mb-1">Triage Priority</label>
                        <select
                          value={walkinPriority}
                          onChange={e => setWalkinPriority(e.target.value as Priority)}
                          className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                        >
                          <option value="NORMAL">Normal Priority (Score: 100)</option>
                          <option value="HIGH">High Priority (Score: 180)</option>
                          <option value="EMERGENCY">Emergency Triage (Score: 300)</option>
                        </select>
                      </div>
                    </div>

                    <button
                      type="submit"
                      className="w-full py-2.5 bg-teal-600 hover:bg-teal-700 text-white font-bold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                    >
                      <UserCheck className="w-4 h-4" />
                      <span>Generate Real-Time Token &amp; Enqueue</span>
                    </button>
                  </form>
                </div>

                {/* Right: Doctor Status Summary */}
                <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-xs">
                  <h3 className="text-base font-bold text-slate-900 mb-3">Live Department Cabins</h3>
                  <div className="space-y-3">
                    {doctors.map(doc => {
                      const docQueue = queue.filter(q => q.doctorName === doc.name && q.status === 'WAITING');
                      return (
                        <div key={doc.id} className="p-3 bg-slate-50 rounded-lg border border-slate-100 flex justify-between items-center">
                          <div>
                            <div className="font-semibold text-xs text-slate-800">{doc.name}</div>
                            <div className="text-[11px] text-slate-400">{doc.dept} &bull; Room {doc.room}</div>
                          </div>
                          <div className="text-right">
                            <span className={`text-[11px] font-bold px-2 py-0.5 rounded-full ${
                              doc.status === 'BUSY' ? 'bg-amber-100 text-amber-800' : 'bg-emerald-100 text-emerald-800'
                            }`}>
                              {doc.status}
                            </span>
                            <div className="text-[10px] text-slate-500 mt-1">{docQueue.length} waiting</div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              </div>

              {/* Roster of Today's Issued Tokens */}
              <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-xs">
                <div className="px-6 py-4 border-b border-slate-200 bg-slate-50 flex justify-between items-center">
                  <h3 className="font-bold text-slate-800 text-sm">Today&apos;s Issued Tokens Roster</h3>
                  <span className="text-xs text-slate-500">Total Tokens: {queue.length}</span>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-xs">
                    <thead className="bg-slate-100 text-slate-600 font-bold border-b border-slate-200 uppercase">
                      <tr>
                        <th className="py-2.5 px-4">Token</th>
                        <th className="py-2.5 px-4">Patient</th>
                        <th className="py-2.5 px-4">Doctor &amp; Room</th>
                        <th className="py-2.5 px-4">Priority</th>
                        <th className="py-2.5 px-4">Status</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {queue.map(item => (
                        <tr key={item.tokenNumber} className="hover:bg-slate-50">
                          <td className="py-2.5 px-4 font-mono font-bold text-slate-900">{item.tokenDisplay}</td>
                          <td className="py-2.5 px-4 font-semibold text-slate-800">{item.patientName}</td>
                          <td className="py-2.5 px-4 text-slate-600">{item.doctorName}</td>
                          <td className="py-2.5 px-4">
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                              item.priority === 'EMERGENCY' ? 'bg-rose-100 text-rose-800' :
                              item.priority === 'HIGH' ? 'bg-amber-100 text-amber-800' : 'bg-slate-100 text-slate-700'
                            }`}>
                              {item.priority}
                            </span>
                          </td>
                          <td className="py-2.5 px-4">
                            <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                              item.status === 'CALLED' ? 'bg-amber-100 text-amber-800' :
                              item.status === 'IN_CONSULTATION' ? 'bg-blue-100 text-blue-800' :
                              item.status === 'COMPLETED' ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-100 text-slate-600'
                            }`}>
                              {item.status}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )
        )}

        {/* ========================================================= */}
        {/* 4. ADMIN CONSOLE (SEPARATE LOGIN PAGE & GOVERNANCE)       */}
        {/* ========================================================= */}
        {activePortal === 'ADMIN' && (
          !adminAuth.isAuthenticated ? (
            /* DEDICATED ADMIN LOGIN PAGE */
            <div className="max-w-md w-full mx-auto my-auto space-y-6">
              <div className="bg-white rounded-2xl border border-slate-200 shadow-xl p-8">
                <div className="text-center mb-6">
                  <div className="w-14 h-14 bg-purple-100 text-purple-700 rounded-2xl flex items-center justify-center mx-auto mb-3 shadow-inner">
                    <Shield className="w-7 h-7" />
                  </div>
                  <h2 className="text-2xl font-extrabold text-slate-900">Hospital Admin Console</h2>
                  <p className="text-xs text-slate-500 mt-1">
                    Executive governance, queue algorithms &amp; audit logging
                  </p>
                </div>

                <form onSubmit={handleAdminLogin} className="space-y-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Administrator Email / Username *
                    </label>
                    <input
                      type="text"
                      value={adminLoginUser}
                      onChange={e => setAdminLoginUser(e.target.value)}
                      placeholder="e.g. admin@hospital.org"
                      required
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-purple-500 focus:ring-2 focus:ring-purple-100"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Master Password *
                    </label>
                    <input
                      type="password"
                      value={adminLoginPass}
                      onChange={e => setAdminLoginPass(e.target.value)}
                      required
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-purple-500 focus:ring-2 focus:ring-purple-100 font-mono"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Administrative Scope
                    </label>
                    <select
                      value={adminLoginScope}
                      onChange={e => setAdminLoginScope(e.target.value)}
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2.5 outline-none focus:border-purple-500 focus:ring-2 focus:ring-purple-100"
                    >
                      <option value="FULL">Hospital Operations Director (Full Access)</option>
                      <option value="CMO">Chief Medical Officer (Clinical Oversight)</option>
                      <option value="IT">IT Systems &amp; Queue Algorithm Engineer</option>
                    </select>
                  </div>

                  <button
                    type="submit"
                    className="w-full py-3 bg-purple-600 hover:bg-purple-700 text-white font-bold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                  >
                    <LogIn className="w-4 h-4" />
                    <span>Authenticate Admin Console</span>
                  </button>
                </form>

                <div className="mt-6 pt-5 border-t border-slate-100">
                  <button
                    type="button"
                    onClick={() => {
                      setAdminLoginUser('admin@hospital.org');
                      setCurrentUserRole('ADMIN');
                      setActivePortal('ADMIN');
                      setAdminAuth({
                        isAuthenticated: true,
                        username: 'admin@hospital.org',
                        roleTitle: 'Hospital Operations Director'
                      });
                      showToast("Logged in as Administrator: Full Hospital Operations Access Granted!");
                    }}
                    className="w-full text-left px-3 py-2 bg-slate-50 hover:bg-purple-50 hover:text-purple-700 border border-slate-200 rounded-lg text-xs flex justify-between items-center transition"
                  >
                    <span className="font-medium">Dr. Arthur Sterling &bull; Operations Director</span>
                    <span className="text-[10px] text-purple-600 font-bold">1-Click Login &rarr;</span>
                  </button>
                </div>
              </div>
            </div>
          ) : (
            /* ADMIN EXECUTIVE CONSOLE DASHBOARD */
            <div className="space-y-6">
              {/* Admin Header Bar */}
              <div className="bg-white rounded-xl border border-slate-200 p-4 flex items-center justify-between shadow-xs">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 rounded-full bg-purple-100 text-purple-700 flex items-center justify-center font-bold">
                    <Shield className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="font-bold text-slate-900 text-sm">
                      {adminAuth.roleTitle} ({adminAuth.username})
                    </div>
                    <div className="text-xs text-slate-500">
                      System Status: <span className="font-medium text-emerald-600">Operational</span>
                    </div>
                  </div>
                </div>

                <button
                  onClick={handleLogout}
                  className="px-3 py-1.5 text-xs font-semibold text-rose-600 hover:bg-rose-50 border border-rose-200 rounded-lg flex items-center space-x-1.5 transition"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  <span>Admin Sign Out</span>
                </button>
              </div>

              {/* Executive KPI Cards */}
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
                  <div className="text-xs text-slate-500 uppercase font-semibold">Total Patients</div>
                  <div className="text-3xl font-extrabold text-slate-900 mt-1 font-mono">1,482</div>
                  <div className="text-xs text-emerald-600 mt-1 font-medium">&uarr; 14 today</div>
                </div>
                <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
                  <div className="text-xs text-slate-500 uppercase font-semibold">Active Doctor Cabins</div>
                  <div className="text-3xl font-extrabold text-blue-700 mt-1 font-mono">4 / 4</div>
                  <div className="text-xs text-slate-400 mt-1">Full shift capacity</div>
                </div>
                <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
                  <div className="text-xs text-slate-500 uppercase font-semibold">Average Wait</div>
                  <div className="text-3xl font-extrabold text-amber-600 mt-1 font-mono">14.2m</div>
                  <div className="text-xs text-emerald-600 mt-1 font-medium">&darr; -2.4m below target</div>
                </div>
                <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
                  <div className="text-xs text-slate-500 uppercase font-semibold">Tokens Today</div>
                  <div className="text-3xl font-extrabold text-emerald-600 mt-1 font-mono">{queue.length}</div>
                  <div className="text-xs text-slate-400 mt-1">Issued tokens</div>
                </div>
              </div>

              {/* Priority & Fairness Settings */}
              <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-xs">
                <h3 className="font-bold text-slate-900 text-base mb-1">Queue Priority &amp; Triage Policy</h3>
                <p className="text-xs text-slate-500 mb-4">Configured priority levels and aging rules.</p>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="p-4 bg-slate-50 rounded-lg border border-slate-200">
                    <div className="text-xs font-bold text-slate-700 uppercase">Emergency Base</div>
                    <div className="text-2xl font-extrabold text-rose-600 font-mono mt-1">Emergency</div>
                    <p className="text-[11px] text-slate-500 mt-1">Immediate consultation bypass</p>
                  </div>
                  <div className="p-4 bg-slate-50 rounded-lg border border-slate-200">
                    <div className="text-xs font-bold text-slate-700 uppercase">High Priority</div>
                    <div className="text-2xl font-extrabold text-amber-600 font-mono mt-1">Urgent</div>
                    <p className="text-[11px] text-slate-500 mt-1">Acute referrals and senior citizens</p>
                  </div>
                  <div className="p-4 bg-slate-50 rounded-lg border border-slate-200">
                    <div className="text-xs font-bold text-slate-700 uppercase">Standard Queue</div>
                    <div className="text-2xl font-extrabold text-blue-600 font-mono mt-1">Normal</div>
                    <p className="text-[11px] text-slate-500 mt-1">Routine consultations in arrival order</p>
                  </div>
                </div>
              </div>
            </div>
          )
        )}

        {/* ========================================================= */}
        {/* 5. ANNOUNCEMENT DASHBOARD (HOSPITAL TV DISPLAY & CHIME)   */}
        {/* ========================================================= */}
        {activePortal === 'ANNOUNCEMENTS' && (
          <div className="space-y-6">
            {/* TV Display Header & Audio Controls */}
            <div className="bg-slate-900 text-white rounded-2xl p-6 shadow-xl border border-slate-800 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
              <div>
                <div className="flex items-center space-x-2">
                  <span className="w-2.5 h-2.5 rounded-full bg-emerald-400"></span>
                  <span className="text-xs font-mono tracking-widest text-emerald-400 uppercase font-bold">
                    WAITING LOUNGE DISPLAY
                  </span>
                </div>
                <h2 className="text-2xl font-bold text-white tracking-tight mt-1">
                  Central Outpatient Display
                </h2>
                <p className="text-slate-400 text-xs mt-0.5">
                  Live queue calling and patient announcements.
                </p>
              </div>

              <div className="flex items-center space-x-3">
                <button
                  onClick={() => {
                    setAudioEnabled(!audioEnabled);
                    showToast(audioEnabled ? "Audio chime muted" : "Audio chime unmuted");
                  }}
                  className={`px-3 py-2 rounded-xl text-xs font-bold flex items-center space-x-2 border transition ${
                    audioEnabled
                      ? 'bg-blue-600/30 text-blue-300 border-blue-500/40 hover:bg-blue-600/50'
                      : 'bg-slate-800 text-slate-400 border-slate-700 hover:bg-slate-750'
                  }`}
                >
                  {audioEnabled ? <Volume2 className="w-4 h-4 text-blue-400" /> : <VolumeX className="w-4 h-4 text-slate-400" />}
                  <span>{audioEnabled ? 'Voice Chime: ACTIVE' : 'Voice Chime: MUTED'}</span>
                </button>

                <button
                  onClick={() => {
                    const serving = currentServingItem || queue[0];
                    announceTokenVoice(
                      serving.tokenDisplay,
                      serving.patientName,
                      'Dr. Rajesh Kumar',
                      'OPD-204'
                    );
                    showToast(`Played live announcement chime for Token #${serving.tokenDisplay}`);
                  }}
                  className="px-4 py-2 bg-amber-500 hover:bg-amber-400 text-slate-950 text-xs font-black rounded-xl shadow-lg transition flex items-center space-x-2"
                >
                  <Play className="w-3.5 h-3.5 fill-slate-950" />
                  <span>Test Audio Call Chime</span>
                </button>

                <button
                  onClick={() => setBroadcastModalOpen(true)}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-xl shadow-lg transition flex items-center space-x-1.5"
                >
                  <Plus className="w-3.5 h-3.5" />
                  <span>Publish Notice</span>
                </button>
              </div>
            </div>

            {/* Now Serving Big Board (Digital Signage Style) */}
            <div className="bg-gradient-to-r from-blue-950 via-slate-900 to-indigo-950 rounded-2xl p-8 border border-blue-800/40 shadow-2xl text-white relative overflow-hidden">
              <div className="absolute top-0 right-0 p-8 opacity-10 font-black text-9xl tracking-tighter select-none pointer-events-none">
                NOW
              </div>

              <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-end gap-6">
                <div>
                  <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-400/20 text-amber-300 border border-amber-400/40 text-xs font-extrabold uppercase tracking-wider mb-2">
                    <Radio className="w-3.5 h-3.5 animate-pulse" />
                    Now Calling In Doctor Cabin
                  </div>
                  <div className="text-6xl md:text-7xl font-black font-mono tracking-tight text-amber-300">
                    {currentServingItem ? currentServingItem.tokenDisplay : 'WAITING'}
                  </div>
                  <div className="text-xl font-bold text-white mt-2">
                    Patient: {currentServingItem ? currentServingItem.patientName : 'Next token being prepared...'}
                  </div>
                  <div className="text-sm text-blue-200 mt-1">
                    Room: <strong className="text-white">OPD-204</strong> &bull; Consultant: <strong className="text-white">Dr. Rajesh Kumar</strong> (Cardiology)
                  </div>
                </div>

                <div className="text-right">
                  <div className="text-xs text-blue-300 uppercase tracking-widest font-semibold">Waiting Lounge Time</div>
                  <div className="text-4xl font-mono font-bold text-white tracking-wider mt-1">{currentTime}</div>
                  <div className="text-xs text-blue-300/80 mt-1">{currentDate}</div>
                </div>
              </div>
            </div>

            {/* Live OPD Doctor Cabins Matrix */}
            <div>
              <div className="flex justify-between items-center mb-3">
                <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider flex items-center gap-2">
                  <Stethoscope className="w-4 h-4 text-blue-600" />
                  <span>Outpatient Consultation Cabins &bull; Live Status</span>
                </h3>
                <span className="text-xs text-slate-500">Auto-refreshed via AJAX every 5s</span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                {doctors.map(doc => {
                  const docWait = queue.filter(q => q.doctorName === doc.name && q.status === 'WAITING').length;
                  const activeServing = queue.find(q => q.doctorName === doc.name && (q.status === 'CALLED' || q.status === 'IN_CONSULTATION'));

                  return (
                    <div
                      key={doc.id}
                      className="bg-white rounded-xl border border-slate-200 p-5 shadow-xs flex flex-col justify-between"
                    >
                      <div>
                        <div className="flex justify-between items-start mb-2">
                          <span className="px-2 py-0.5 bg-slate-100 text-slate-700 rounded text-xs font-mono font-bold">
                            {doc.room}
                          </span>
                          <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                            doc.status === 'BUSY' ? 'bg-amber-100 text-amber-800' : 'bg-emerald-100 text-emerald-800'
                          }`}>
                            {doc.status === 'BUSY' ? 'IN CONSULTATION' : 'AVAILABLE'}
                          </span>
                        </div>
                        <h4 className="font-bold text-slate-900 text-sm">{doc.name}</h4>
                        <div className="text-xs text-slate-500">{doc.dept}</div>

                        <div className="mt-4 p-3 bg-slate-50 rounded-lg border border-slate-100">
                          <div className="text-[10px] text-slate-400 uppercase font-semibold">Currently Calling:</div>
                          <div className="font-mono text-base font-extrabold text-blue-700 mt-0.5">
                            {activeServing ? activeServing.tokenDisplay : 'Ready for Patient'}
                          </div>
                          <div className="text-[11px] text-slate-600 truncate">
                            {activeServing ? activeServing.patientName : 'No queue backlog'}
                          </div>
                        </div>
                      </div>

                      <div className="mt-3 pt-3 border-t border-slate-100 flex justify-between items-center text-xs text-slate-500">
                        <span>Waiting Queue:</span>
                        <span className="font-bold text-slate-800 font-mono">{docWait} patients</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Upcoming Tokens Matrix & Hospital Announcements Bulletin */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Left 2 Cols: Next Tokens in Line */}
              <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 overflow-hidden shadow-xs">
                <div className="px-6 py-4 border-b border-slate-200 bg-slate-50 flex justify-between items-center">
                  <h3 className="font-bold text-slate-800 text-sm flex items-center gap-2">
                    <Clock className="w-4 h-4 text-emerald-600" />
                    <span>Next Tokens in Line (Expected Calling Order)</span>
                  </h3>
                  <span className="text-xs text-slate-500 font-mono">Real-Time Estimation Engine</span>
                </div>
                <div className="p-4 grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {queue.filter(q => q.status === 'WAITING').map((item, idx) => (
                    <div
                      key={item.tokenNumber}
                      className="p-3 bg-slate-50 rounded-xl border border-slate-200 flex items-center justify-between hover:bg-blue-50/50 transition"
                    >
                      <div className="flex items-center space-x-3">
                        <div className="w-8 h-8 rounded-lg bg-blue-600 text-white font-mono font-bold flex items-center justify-center text-xs shadow-xs">
                          #{idx + 1}
                        </div>
                        <div>
                          <div className="font-mono font-extrabold text-sm text-slate-900">{item.tokenDisplay}</div>
                          <div className="text-xs text-slate-600">{item.patientName}</div>
                          <div className="text-[10px] text-slate-400">{item.doctorName}</div>
                        </div>
                      </div>

                      <div className="text-right">
                        <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                          item.priority === 'EMERGENCY' ? 'bg-rose-100 text-rose-800' :
                          item.priority === 'HIGH' ? 'bg-amber-100 text-amber-800' : 'bg-slate-200 text-slate-700'
                        }`}>
                          {item.priority}
                        </span>
                        <div className="text-xs font-mono font-semibold text-emerald-700 mt-1">
                          ~{calculateWaitTime(item.tokenNumber)} min
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Right Col: Public Bulletins & Announcements Feed */}
              <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-xs flex flex-col justify-between">
                <div>
                  <div className="flex justify-between items-center mb-4">
                    <h3 className="font-bold text-slate-900 text-sm flex items-center gap-2">
                      <Megaphone className="w-4 h-4 text-amber-500" />
                      <span>Hospital Public Bulletins</span>
                    </h3>
                    {currentUserRole !== 'PATIENT' ? (
                      <button
                        onClick={() => setBroadcastModalOpen(true)}
                        className="px-2.5 py-1 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold rounded-lg text-xs flex items-center gap-1 shadow-xs transition"
                      >
                        <Plus className="w-3.5 h-3.5" />
                        <span>Broadcast Notice</span>
                      </button>
                    ) : (
                      <span className="text-[11px] font-medium text-slate-400 bg-slate-100 px-2 py-0.5 rounded border border-slate-200">
                        Patient Mode: View Only
                      </span>
                    )}
                  </div>

                  <div className="space-y-3 overflow-y-auto max-h-[380px] pr-1">
                    {announcements.map(notice => (
                      <div
                        key={notice.id}
                        className={`p-3 rounded-xl border text-xs space-y-1 ${
                          notice.category === 'URGENT'
                            ? 'bg-rose-50 border-rose-200 text-rose-900'
                            : notice.category === 'OPD'
                            ? 'bg-blue-50 border-blue-200 text-blue-900'
                            : notice.category === 'CLINICAL'
                            ? 'bg-purple-50 border-purple-200 text-purple-900'
                            : 'bg-slate-50 border-slate-200 text-slate-800'
                        }`}
                      >
                        <div className="flex justify-between items-center">
                          <span className={`text-[10px] font-extrabold uppercase px-1.5 py-0.2 rounded ${
                            notice.category === 'URGENT' ? 'bg-rose-200 text-rose-900' :
                            notice.category === 'OPD' ? 'bg-blue-200 text-blue-900' : 'bg-slate-200 text-slate-700'
                          }`}>
                            {notice.category}
                          </span>
                          <span className="text-[10px] text-slate-400">{notice.time}</span>
                        </div>
                        <div className="font-bold text-xs">{notice.title}</div>
                        <p className="text-[11px] leading-relaxed opacity-90">{notice.message}</p>
                        <div className="text-[10px] text-slate-400 pt-1 font-mono">By: {notice.author}</div>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="mt-4 pt-3 border-t border-slate-100 text-center">
                  <button
                    onClick={() => {
                      playHospitalChime();
                      showToast("Chime sounded through waiting area speakers.");
                    }}
                    className="w-full py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-lg text-xs flex items-center justify-center space-x-1.5 transition"
                  >
                    <Volume2 className="w-3.5 h-3.5 text-blue-600" />
                    <span>Trigger Lounge Bell Chime</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
          </>
        )}

      </main>

      {/* Broadcast Announcement Modal */}
      {broadcastModalOpen && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <div className="flex justify-between items-center border-b border-slate-200 pb-3">
              <h3 className="font-bold text-slate-900 text-base flex items-center gap-2">
                <Megaphone className="w-5 h-5 text-amber-500" />
                <span>Publish Hospital Announcement</span>
              </h3>
              <button
                onClick={() => setBroadcastModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 font-bold text-lg"
              >
                &times;
              </button>
            </div>

            <form onSubmit={handlePublishAnnouncement} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Notice Category</label>
                <select
                  value={newNoticeCategory}
                  onChange={e => setNewNoticeCategory(e.target.value as unknown as HospitalAnnouncement['category'])}
                  className="w-full text-xs border border-slate-300 rounded-lg p-2 outline-none focus:border-blue-500"
                >
                  <option value="OPD">OPD Schedule &amp; Cabins</option>
                  <option value="URGENT">Emergency &amp; Triage Alert</option>
                  <option value="GENERAL">General Hospital Amenities</option>
                  <option value="CLINICAL">Pharmacy &amp; Diagnostics</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Headline / Title *</label>
                <input
                  type="text"
                  value={newNoticeTitle}
                  onChange={e => setNewNoticeTitle(e.target.value)}
                  placeholder="e.g. Cardiology OPD Temporary Pause"
                  required
                  className="w-full text-xs border border-slate-300 rounded-lg p-2.5 outline-none focus:border-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Announcement Message *</label>
                <textarea
                  value={newNoticeMsg}
                  onChange={e => setNewNoticeMsg(e.target.value)}
                  placeholder="Enter details broadcasted across all waiting monitors..."
                  rows={3}
                  required
                  className="w-full text-xs border border-slate-300 rounded-lg p-2.5 outline-none focus:border-blue-500"
                />
              </div>

              <div className="flex justify-end space-x-2 pt-3 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setBroadcastModalOpen(false)}
                  className="px-4 py-2 border border-slate-300 rounded-lg text-xs font-semibold text-slate-700 hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-xs font-bold shadow-sm"
                >
                  Broadcast Live Notice
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Doctor Complete Consultation Modal */}
      {showConsultModal && activeConsultToken && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl space-y-4">
            <div className="flex justify-between items-center border-b border-slate-200 pb-3">
              <div>
                <h3 className="font-bold text-slate-900 text-base">Complete Consultation &bull; {activeConsultToken.tokenDisplay}</h3>
                <p className="text-xs text-slate-500">Patient: {activeConsultToken.patientName} ({activeConsultToken.patientUhid})</p>
              </div>
              <button
                onClick={() => setShowConsultModal(false)}
                className="text-slate-400 hover:text-slate-600 font-bold text-lg"
              >
                &times;
              </button>
            </div>

            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Clinical Diagnosis *</label>
                <input
                  type="text"
                  value={diagnosisText}
                  onChange={e => setDiagnosisText(e.target.value)}
                  className="w-full text-sm border border-slate-300 rounded-lg p-2.5 outline-none focus:border-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Prescription &amp; Orders *</label>
                <textarea
                  value={prescriptionText}
                  onChange={e => setPrescriptionText(e.target.value)}
                  rows={3}
                  className="w-full text-sm border border-slate-300 rounded-lg p-2.5 outline-none focus:border-blue-500 font-mono text-xs"
                />
              </div>
            </div>

            <div className="flex justify-end space-x-2 pt-3 border-t border-slate-200">
              <button
                onClick={() => setShowConsultModal(false)}
                className="px-4 py-2 border border-slate-300 rounded-lg text-xs font-semibold text-slate-700 hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                onClick={handleSaveConsultation}
                className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-bold shadow-sm"
              >
                Save &amp; Mark Completed
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Floating Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-6 right-6 z-50 flex items-center space-x-2 bg-slate-900 text-white px-4 py-3 rounded-xl shadow-2xl border border-slate-700 animate-in fade-in slide-in-from-bottom-2 duration-200">
          <Bell className="w-4 h-4 text-blue-400 shrink-0" />
          <span className="text-xs font-medium">{toastMessage}</span>
          <button
            onClick={() => setToastMessage(null)}
            className="ml-3 text-slate-400 hover:text-white text-sm font-bold"
          >
            &times;
          </button>
        </div>
      )}
    </div>
  );
}
