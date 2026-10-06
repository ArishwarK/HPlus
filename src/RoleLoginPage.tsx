import React from 'react';
import {
  User,
  Stethoscope,
  Building2,
  Shield,
  LogIn,
  Megaphone,
  ArrowRight
} from 'lucide-react';
import { UserRole } from './App';

interface RoleLoginPageProps {
  selectedRole: UserRole;
  onSelectRole: (role: UserRole) => void;
  // Patient form
  patientToken: string;
  setPatientToken: (val: string) => void;
  patientPhone: string;
  setPatientPhone: (val: string) => void;
  patientTab: 'TOKEN' | 'UHID';
  setPatientTab: (tab: 'TOKEN' | 'UHID') => void;
  patientUhid: string;
  setPatientUhid: (val: string) => void;
  onPatientSubmit: (e?: React.FormEvent) => void;
  onQuickPatientLogin: (token?: string) => void;
  // Doctor form
  doctorUser: string;
  setDoctorUser: (val: string) => void;
  doctorPass: string;
  setDoctorPass: (val: string) => void;
  doctorRoom: string;
  setDoctorRoom: (val: string) => void;
  doctors: Array<{ id: number; name: string; dept: string; room: string }>;
  onDoctorSubmit: (e?: React.FormEvent) => void;
  onQuickDoctorLogin: (doctorId?: number) => void;
  // Reception form
  receptionUser: string;
  setReceptionUser: (val: string) => void;
  receptionPass: string;
  setReceptionPass: (val: string) => void;
  receptionCounter: string;
  setReceptionCounter: (val: string) => void;
  onReceptionSubmit: (e?: React.FormEvent) => void;
  onQuickReceptionLogin: (counter?: string) => void;
  // Admin form
  adminUser: string;
  setAdminUser: (val: string) => void;
  adminPass: string;
  setAdminPass: (val: string) => void;
  onAdminSubmit: (e?: React.FormEvent) => void;
  onQuickAdminLogin: () => void;
  // Announcements
  onOpenAnnouncements: () => void;
}

export const RoleLoginPage: React.FC<RoleLoginPageProps> = ({
  selectedRole,
  onSelectRole,
  patientToken,
  setPatientToken,
  patientPhone,
  setPatientPhone,
  patientTab,
  setPatientTab,
  patientUhid,
  setPatientUhid,
  onPatientSubmit,
  onQuickPatientLogin,
  doctorUser,
  setDoctorUser,
  doctorPass,
  setDoctorPass,
  doctorRoom,
  setDoctorRoom,
  doctors,
  onDoctorSubmit,
  onQuickDoctorLogin,
  receptionUser,
  setReceptionUser,
  receptionPass,
  setReceptionPass,
  receptionCounter,
  setReceptionCounter,
  onReceptionSubmit,
  onQuickReceptionLogin,
  adminUser,
  setAdminUser,
  adminPass,
  setAdminPass,
  onAdminSubmit,
  onQuickAdminLogin,
  onOpenAnnouncements
}) => {
  return (
    <div className="max-w-5xl w-full mx-auto my-auto space-y-8 py-2">
      {/* Header */}
      <div className="text-center space-y-2">
        <h1 className="text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight">
          Hospital Portal
        </h1>
        <p className="text-sm text-slate-500 max-w-lg mx-auto">
          Select your role to access the queue management system.
        </p>
      </div>

      {/* Role-Specific Login Form Box */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden max-w-xl w-full mx-auto">
        {/* Role Tabs */}
        <div className="grid grid-cols-4 bg-slate-50 p-1 border-b border-slate-200">
          <button
            type="button"
            onClick={() => onSelectRole('PATIENT')}
            className={`py-2 text-xs font-semibold rounded-lg transition ${
              selectedRole === 'PATIENT' ? 'bg-white text-blue-700 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Patient
          </button>
          <button
            type="button"
            onClick={() => onSelectRole('DOCTOR')}
            className={`py-2 text-xs font-semibold rounded-lg transition ${
              selectedRole === 'DOCTOR' ? 'bg-white text-indigo-700 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Doctor
          </button>
          <button
            type="button"
            onClick={() => onSelectRole('RECEPTIONIST')}
            className={`py-2 text-xs font-semibold rounded-lg transition ${
              selectedRole === 'RECEPTIONIST' ? 'bg-white text-teal-700 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Reception
          </button>
          <button
            type="button"
            onClick={() => onSelectRole('ADMIN')}
            className={`py-2 text-xs font-semibold rounded-lg transition ${
              selectedRole === 'ADMIN' ? 'bg-white text-purple-700 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Admin
          </button>
        </div>

        <div className="p-6">
          {/* Form for PATIENT */}
          {selectedRole === 'PATIENT' && (
            <div className="space-y-4">
              <div className="flex rounded-lg bg-slate-100 p-1">
                <button
                  type="button"
                  onClick={() => setPatientTab('TOKEN')}
                  className={`flex-1 py-1.5 text-xs font-semibold rounded-md transition ${
                    patientTab === 'TOKEN' ? 'bg-white text-blue-600 shadow-xs' : 'text-slate-500 hover:text-slate-900'
                  }`}
                >
                  Token &amp; Phone
                </button>
                <button
                  type="button"
                  onClick={() => setPatientTab('UHID')}
                  className={`flex-1 py-1.5 text-xs font-semibold rounded-md transition ${
                    patientTab === 'UHID' ? 'bg-white text-blue-600 shadow-xs' : 'text-slate-500 hover:text-slate-900'
                  }`}
                >
                  Patient UHID / MRN
                </button>
              </div>

              <form onSubmit={onPatientSubmit} className="space-y-3">
                {patientTab === 'TOKEN' ? (
                  <>
                    <div>
                      <label className="block text-xs font-medium text-slate-700 mb-1">
                        Token Number
                      </label>
                      <input
                        type="text"
                        value={patientToken}
                        onChange={e => setPatientToken(e.target.value)}
                        placeholder="e.g. 45"
                        required
                        className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 font-mono"
                      />
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-slate-700 mb-1">
                        Phone Number
                      </label>
                      <input
                        type="tel"
                        value={patientPhone}
                        onChange={e => setPatientPhone(e.target.value)}
                        placeholder="e.g. +1 (555) 234-5678"
                        required
                        className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
                      />
                    </div>
                  </>
                ) : (
                  <div>
                    <label className="block text-xs font-medium text-slate-700 mb-1">
                      Patient UHID
                    </label>
                    <input
                      type="text"
                      value={patientUhid}
                      onChange={e => setPatientUhid(e.target.value)}
                      placeholder="e.g. UHID-2026-0045"
                      required
                      className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 font-mono"
                    />
                  </div>
                )}

                <button
                  type="submit"
                  className="w-full py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                >
                  <LogIn className="w-4 h-4" />
                  <span>Sign In as Patient</span>
                </button>
              </form>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between">
                <span className="text-xs text-slate-400">Demo Account:</span>
                <button
                  type="button"
                  onClick={() => onQuickPatientLogin('45')}
                  className="text-xs font-medium text-blue-600 hover:text-blue-800"
                >
                  Token #45 (Robert Chang)
                </button>
              </div>
            </div>
          )}

          {/* Form for DOCTOR */}
          {selectedRole === 'DOCTOR' && (
            <div className="space-y-4">
              <form onSubmit={onDoctorSubmit} className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    Email / Doctor ID
                  </label>
                  <input
                    type="text"
                    value={doctorUser}
                    onChange={e => setDoctorUser(e.target.value)}
                    placeholder="e.g. dr.kumar@hospital.org"
                    required
                    className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    Password
                  </label>
                  <input
                    type="password"
                    value={doctorPass}
                    onChange={e => setDoctorPass(e.target.value)}
                    required
                    className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    OPD Cabin
                  </label>
                  <select
                    value={doctorRoom}
                    onChange={e => setDoctorRoom(e.target.value)}
                    className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
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
                  className="w-full py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                >
                  <LogIn className="w-4 h-4" />
                  <span>Sign In as Doctor</span>
                </button>
              </form>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between">
                <span className="text-xs text-slate-400">Demo Account:</span>
                <button
                  type="button"
                  onClick={() => onQuickDoctorLogin(1)}
                  className="text-xs font-medium text-indigo-600 hover:text-indigo-800"
                >
                  Dr. Rajesh Kumar (OPD-204)
                </button>
              </div>
            </div>
          )}

          {/* Form for RECEPTIONIST */}
          {selectedRole === 'RECEPTIONIST' && (
            <div className="space-y-4">
              <form onSubmit={onReceptionSubmit} className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    Staff ID
                  </label>
                  <input
                    type="text"
                    value={receptionUser}
                    onChange={e => setReceptionUser(e.target.value)}
                    placeholder="e.g. REC-4012"
                    required
                    className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-teal-500 focus:ring-1 focus:ring-teal-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    Password
                  </label>
                  <input
                    type="password"
                    value={receptionPass}
                    onChange={e => setReceptionPass(e.target.value)}
                    required
                    className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-teal-500 focus:ring-1 focus:ring-teal-500 font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    Counter
                  </label>
                  <select
                    value={receptionCounter}
                    onChange={e => setReceptionCounter(e.target.value)}
                    className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-teal-500 focus:ring-1 focus:ring-teal-500"
                  >
                    <option value="1">Counter 1 - Central OPD Registration</option>
                    <option value="2">Counter 2 - Fast Track</option>
                    <option value="3">Counter 3 - Pediatric &amp; Emergency</option>
                  </select>
                </div>

                <button
                  type="submit"
                  className="w-full py-2.5 bg-teal-600 hover:bg-teal-700 text-white font-semibold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                >
                  <LogIn className="w-4 h-4" />
                  <span>Sign In to Reception</span>
                </button>
              </form>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between">
                <span className="text-xs text-slate-400">Demo Account:</span>
                <button
                  type="button"
                  onClick={() => onQuickReceptionLogin('1')}
                  className="text-xs font-medium text-teal-600 hover:text-teal-800"
                >
                  Sarah Jenkins (Counter 1)
                </button>
              </div>
            </div>
          )}

          {/* Form for ADMIN */}
          {selectedRole === 'ADMIN' && (
            <div className="space-y-4">
              <form onSubmit={onAdminSubmit} className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    Admin Email
                  </label>
                  <input
                    type="email"
                    value={adminUser}
                    onChange={e => setAdminUser(e.target.value)}
                    placeholder="e.g. admin@hospital.org"
                    required
                    className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">
                    Password
                  </label>
                  <input
                    type="password"
                    value={adminPass}
                    onChange={e => setAdminPass(e.target.value)}
                    required
                    className="w-full text-sm border border-slate-300 rounded-lg px-3 py-2 outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 font-mono"
                  />
                </div>

                <button
                  type="submit"
                  className="w-full py-2.5 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg shadow-sm transition text-sm flex items-center justify-center space-x-2"
                >
                  <LogIn className="w-4 h-4" />
                  <span>Sign In as Administrator</span>
                </button>
              </form>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between">
                <span className="text-xs text-slate-400">Demo Account:</span>
                <button
                  type="button"
                  onClick={onQuickAdminLogin}
                  className="text-xs font-medium text-purple-600 hover:text-purple-800"
                >
                  Hospital Director
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Public Lounge Announcement Board Shortcut */}
      <div className="max-w-xl w-full mx-auto text-center">
        <button
          type="button"
          onClick={onOpenAnnouncements}
          className="inline-flex items-center space-x-2 text-xs font-medium text-slate-600 hover:text-slate-900 bg-white border border-slate-200 hover:border-slate-300 px-4 py-2 rounded-lg transition shadow-2xs"
        >
          <Megaphone className="w-4 h-4 text-amber-500" />
          <span>View Public Waiting Lounge Display</span>
        </button>
      </div>
    </div>
  );
};
