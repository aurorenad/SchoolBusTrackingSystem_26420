import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/auth/Login';
import ForgotPassword from './pages/auth/ForgotPassword';
import SetPassword from './pages/auth/SetPassword';
import AdminLayout from './pages/admin/AdminLayout';
import DashboardHome from './pages/admin/DashboardHome';
import Buses from './pages/admin/Buses';
import Drivers from './pages/admin/Drivers';
import RoutesPage from './pages/admin/RoutesPage';
import Students from './pages/admin/Students';
import Schedules from './pages/admin/Schedules';
import Announcements from './pages/admin/Announcements';
import Locations from './pages/admin/Locations';
import DriverLayout from './pages/driver/DriverLayout';
import DriverDashboard from './pages/driver/DriverDashboard';
import DriverAssignments from './pages/driver/DriverAssignments';
import DriverSchedule from './pages/driver/DriverSchedule';
import DriverAnnouncements from './pages/driver/DriverAnnouncements';
import StudentLayout from './pages/student/StudentLayout';
import StudentSchedule from './pages/student/StudentSchedule';
import StudentProfile from './pages/student/StudentProfile';
import StudentAnnouncements from './pages/student/StudentAnnouncements';

const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, token } = useAuth();

  if (!token) return <Navigate to="/login" />;
  if (user && allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/" />;
  }
  return children;
};

function App() {
  return (
    <Router>
      <AuthProvider>
        <Toaster position="top-right" />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/set-password" element={<SetPassword />} />

          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<DashboardHome />} />
            <Route path="buses" element={<Buses />} />
            <Route path="drivers" element={<Drivers />} />
            <Route path="routes" element={<RoutesPage />} />
            <Route path="students" element={<Students />} />
            <Route path="schedules" element={<Schedules />} />
            <Route path="announcements" element={<Announcements />} />
            <Route path="locations" element={<Locations />} />
          </Route>

          <Route
            path="/driver"
            element={
              <ProtectedRoute allowedRoles={['DRIVER']}>
                <DriverLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<DriverDashboard />} />
            <Route path="assignments" element={<DriverAssignments />} />
            <Route path="schedule" element={<DriverSchedule />} />
            <Route path="announcements" element={<DriverAnnouncements />} />
          </Route>

          <Route
            path="/student"
            element={
              <ProtectedRoute allowedRoles={['STUDENT', 'PARENT']}>
                <StudentLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/student/schedule" replace />} />
            <Route path="schedule" element={<StudentSchedule />} />
            <Route path="profile" element={<StudentProfile />} />
            <Route path="announcements" element={<StudentAnnouncements />} />
          </Route>

          <Route path="/" element={<Navigate to="/login" />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;