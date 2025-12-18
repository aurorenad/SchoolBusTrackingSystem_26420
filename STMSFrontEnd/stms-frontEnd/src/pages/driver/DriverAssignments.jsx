import React, { useState, useEffect } from 'react';
import { Users, MapPin, CheckCircle, UserX } from 'lucide-react';
import { studentService } from '../../services/studentService';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const DriverAssignments = () => {
    const { user } = useAuth();
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (user?.email) {
            fetchStudents();
        }
    }, [user]);

    const fetchStudents = async () => {
        setLoading(true);
        try {
            const studentsData = await studentService.getByDriverEmail(user.email);
            setStudents(studentsData || []);
        } catch (error) {
            console.error('Error fetching students:', error);
            toast.error('Failed to load students');
        } finally {
            setLoading(false);
        }
    };

    const handleStatusUpdate = async (studentId, newStatus) => {
        try {
            await studentService.updateStatusByDriver(studentId, newStatus);
            toast.success('Student status updated successfully');
            // Refresh students list
            await fetchStudents();
        } catch (error) {
            console.error('Error updating student status:', error);
            const errorMsg = error?.response?.data || error?.message || 'Failed to update student status';
            toast.error(typeof errorMsg === 'string' ? errorMsg : 'Failed to update student status');
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'ONBUS':
                return 'bg-green-100 text-green-700 border-green-200';
            case 'DROPPEDOFF':
                return 'bg-blue-100 text-blue-700 border-blue-200';
            case 'ABSENT':
                return 'bg-red-100 text-red-700 border-red-200';
            default:
                return 'bg-gray-100 text-gray-700 border-gray-200';
        }
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'ONBUS':
                return <CheckCircle className="h-4 w-4" />;
            case 'DROPPEDOFF':
                return <CheckCircle className="h-4 w-4" />;
            case 'ABSENT':
                return <UserX className="h-4 w-4" />;
            default:
                return null;
        }
    };

    return (
        <div>
            <div className="mb-6">
                <h1 className="text-3xl font-bold text-gray-900">Student Assignments</h1>
                <p className="text-gray-600 mt-1">View and manage students assigned to your bus</p>
            </div>

            <div className="bg-white rounded-xl shadow-md overflow-hidden">
                <div className="bg-emerald-700 p-6 text-white">
                    <div className="flex items-center gap-2">
                        <Users className="h-6 w-6" />
                        <h2 className="text-2xl font-bold">Students to Pick Up</h2>
                        {students.length > 0 && (
                            <span className="bg-white/20 px-3 py-1 rounded-full text-sm">
                                {students.length}
                            </span>
                        )}
                    </div>
                </div>
                <div className="p-6">
                    {loading ? (
                        <div className="text-center py-8 text-gray-500">Loading...</div>
                    ) : students.length > 0 ? (
                        <div className="space-y-4">
                            {students.map((student) => (
                                <div key={student.id} className="border border-gray-200 rounded-lg p-5 hover:bg-gray-50 transition">
                                    <div className="flex items-start justify-between mb-4">
                                        <div className="flex-1">
                                            <h3 className="text-lg font-semibold text-gray-900">{student.name}</h3>
                                            <p className="text-sm text-gray-600 mt-1">Class: {student.className || 'N/A'}</p>
                                        </div>
                                        <span className={`px-3 py-1.5 rounded-full text-xs font-medium flex items-center gap-1.5 border ${getStatusColor(student.status)}`}>
                                            {getStatusIcon(student.status)}
                                            {student.status || 'ABSENT'}
                                        </span>
                                    </div>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                                        <div className="flex items-start gap-2">
                                            <MapPin className="h-5 w-5 text-emerald-700 mt-0.5 flex-shrink-0" />
                                            <div>
                                                <p className="text-xs text-gray-500">Pickup Point</p>
                                                <p className="text-sm font-medium text-gray-900">{student.pickUpPoint || 'Not specified'}</p>
                                            </div>
                                        </div>
                                        <div className="flex items-start gap-2">
                                            <MapPin className="h-5 w-5 text-blue-700 mt-0.5 flex-shrink-0" />
                                            <div>
                                                <p className="text-xs text-gray-500">Drop-off Point</p>
                                                <p className="text-sm font-medium text-gray-900">{student.dropOffPoint || 'Not specified'}</p>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex gap-2 pt-4 border-t border-gray-200">
                                        <button
                                            onClick={() => handleStatusUpdate(student.id, 'ONBUS')}
                                            className={`flex-1 px-4 py-2.5 rounded-lg text-sm font-medium transition ${
                                                student.status === 'ONBUS'
                                                    ? 'bg-green-600 text-white shadow-md'
                                                    : 'bg-green-100 text-green-700 hover:bg-green-200'
                                            }`}
                                        >
                                            On Bus
                                        </button>
                                        <button
                                            onClick={() => handleStatusUpdate(student.id, 'DROPPEDOFF')}
                                            className={`flex-1 px-4 py-2.5 rounded-lg text-sm font-medium transition ${
                                                student.status === 'DROPPEDOFF'
                                                    ? 'bg-blue-600 text-white shadow-md'
                                                    : 'bg-blue-100 text-blue-700 hover:bg-blue-200'
                                            }`}
                                        >
                                            Dropped Off
                                        </button>
                                        <button
                                            onClick={() => handleStatusUpdate(student.id, 'ABSENT')}
                                            className={`flex-1 px-4 py-2.5 rounded-lg text-sm font-medium transition ${
                                                student.status === 'ABSENT'
                                                    ? 'bg-red-600 text-white shadow-md'
                                                    : 'bg-red-100 text-red-700 hover:bg-red-200'
                                            }`}
                                        >
                                            Absent
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-12 text-gray-500">
                            <Users className="h-16 w-16 mx-auto mb-4 text-gray-400" />
                            <p className="text-xl font-medium">No students assigned</p>
                            <p className="text-sm mt-2">Students assigned to your bus will appear here</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default DriverAssignments;
