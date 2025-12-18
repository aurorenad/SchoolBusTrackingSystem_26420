import React, { useState, useEffect } from 'react';
import { User, MapPin, GraduationCap, Bus, Edit2, Save, X } from 'lucide-react';
import { studentService } from '../../services/studentService';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const StudentProfile = () => {
    const { user } = useAuth();
    const [student, setStudent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        className: '',
        pickUpPoint: '',
        dropOffPoint: '',
        status: 'ABSENT'
    });

    useEffect(() => {
        if (user?.email) {
            fetchStudent();
        }
    }, [user]);

    const fetchStudent = async () => {
        setLoading(true);
        try {
            const studentsData = await studentService.getAll();
            const currentStudent = studentsData.find(s => s.email?.toLowerCase() === user.email?.toLowerCase());
            if (currentStudent) {
                setStudent(currentStudent);
                setFormData({
                    name: currentStudent.name || '',
                    className: currentStudent.className || '',
                    pickUpPoint: currentStudent.pickUpPoint || '',
                    dropOffPoint: currentStudent.dropOffPoint || '',
                    status: currentStudent.status || 'ABSENT'
                });
            }
        } catch (error) {
            console.error('Error fetching student:', error);
            toast.error('Failed to load profile');
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async () => {
        if (!student) return;

        try {
            await studentService.update(student.id, formData);
            toast.success('Profile updated successfully');
            setIsEditing(false);
            await fetchStudent();
        } catch (error) {
            console.error('Error updating profile:', error);
            const errorMsg = error?.response?.data || error?.message || 'Failed to update profile';
            toast.error(typeof errorMsg === 'string' ? errorMsg : 'Failed to update profile');
        }
    };

    const handleStatusChange = async (newStatus) => {
        if (!student) return;

        try {
            await studentService.updateStatus(student.id, newStatus);
            toast.success('Status updated successfully');
            await fetchStudent();
        } catch (error) {
            console.error('Error updating status:', error);
            toast.error('Failed to update status');
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'ONBUS':
                return 'bg-green-100 text-green-700 border-green-200';
            case 'DROPPEDOFF':
                return 'bg-emerald-100 text-emerald-700 border-emerald-200';
            case 'ABSENT':
                return 'bg-red-100 text-red-700 border-red-200';
            default:
                return 'bg-gray-100 text-gray-700 border-gray-200';
        }
    };

    if (loading) {
        return (
            <div className="bg-white rounded-xl shadow-md p-12 text-center">
                <div className="text-gray-500">Loading profile...</div>
            </div>
        );
    }

    if (!student) {
        return (
            <div className="bg-white rounded-xl shadow-md p-12 text-center">
                <User className="h-16 w-16 mx-auto mb-4 text-gray-400" />
                <p className="text-xl font-medium text-gray-900">Profile not found</p>
            </div>
        );
    }

    return (
        <div>
            <div className="mb-6">
                <h1 className="text-3xl font-bold text-gray-900">My Profile</h1>
                <p className="text-gray-600 mt-1">View and manage your profile information</p>
            </div>

            <div className="bg-white rounded-xl shadow-md overflow-hidden">
                <div className="bg-emerald-700 p-6 text-white">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="bg-white/20 p-3 rounded-lg">
                                <User className="h-6 w-6" />
                            </div>
                            <div>
                                <h2 className="text-2xl font-bold">{student.name}</h2>
                                <p className="opacity-90">{student.email}</p>
                            </div>
                        </div>
                        {!isEditing ? (
                            <button
                                onClick={() => setIsEditing(true)}
                                className="flex items-center gap-2 px-4 py-2 bg-white/20 hover:bg-white/30 rounded-lg transition"
                            >
                                <Edit2 className="h-4 w-4" />
                                Edit Profile
                            </button>
                        ) : (
                            <div className="flex gap-2">
                                <button
                                    onClick={() => {
                                        setIsEditing(false);
                                        setFormData({
                                            name: student.name || '',
                                            className: student.className || '',
                                            pickUpPoint: student.pickUpPoint || '',
                                            dropOffPoint: student.dropOffPoint || '',
                                            status: student.status || 'ABSENT'
                                        });
                                    }}
                                    className="flex items-center gap-2 px-4 py-2 bg-white/20 hover:bg-white/30 rounded-lg transition"
                                >
                                    <X className="h-4 w-4" />
                                    Cancel
                                </button>
                                <button
                                    onClick={handleSave}
                                    className="flex items-center gap-2 px-4 py-2 bg-white text-emerald-700 hover:bg-gray-100 rounded-lg transition font-medium"
                                >
                                    <Save className="h-4 w-4" />
                                    Save
                                </button>
                            </div>
                        )}
                    </div>
                </div>

                <div className="p-6 space-y-6">
                    {/* Basic Information */}
                    <div>
                        <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-2 border-b border-gray-200">Basic Information</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                                {isEditing ? (
                                    <input
                                        type="text"
                                        value={formData.name}
                                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                        className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    />
                                ) : (
                                    <p className="text-gray-900 font-medium">{student.name}</p>
                                )}
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                                <p className="text-gray-900 font-medium">{student.email}</p>
                                <p className="text-xs text-gray-500 mt-1">Email cannot be changed</p>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Class</label>
                                {isEditing ? (
                                    <input
                                        type="text"
                                        value={formData.className}
                                        onChange={(e) => setFormData({ ...formData, className: e.target.value })}
                                        className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    />
                                ) : (
                                    <p className="text-gray-900 font-medium">{student.className || 'N/A'}</p>
                                )}
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Location</label>
                                <p className="text-gray-900 font-medium">{student.location?.name || 'N/A'}</p>
                            </div>
                        </div>
                    </div>

                    {/* Transport Points */}
                    <div>
                        <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-2 border-b border-gray-200">Transport Points</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Pick Up Point</label>
                                {isEditing ? (
                                    <input
                                        type="text"
                                        value={formData.pickUpPoint}
                                        onChange={(e) => setFormData({ ...formData, pickUpPoint: e.target.value })}
                                        className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                        placeholder="Enter pickup location"
                                    />
                                ) : (
                                    <p className="text-gray-900 font-medium">{student.pickUpPoint || 'Not specified'}</p>
                                )}
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Drop Off Point</label>
                                {isEditing ? (
                                    <input
                                        type="text"
                                        value={formData.dropOffPoint}
                                        onChange={(e) => setFormData({ ...formData, dropOffPoint: e.target.value })}
                                        className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                        placeholder="Enter drop-off location"
                                    />
                                ) : (
                                    <p className="text-gray-900 font-medium">{student.dropOffPoint || 'Not specified'}</p>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Bus Assignment */}
                    <div>
                        <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-2 border-b border-gray-200">Bus Assignment</h3>
                        <div className="flex items-center gap-3">
                            <Bus className="h-5 w-5 text-emerald-700" />
                            <div>
                                <p className="text-sm text-gray-500">Assigned Bus</p>
                                <p className="text-lg font-semibold text-gray-900">
                                    {student.bus?.plateNumber || 'Not assigned'}
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Status Management */}
                    <div>
                        <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-2 border-b border-gray-200">Status</h3>
                        <div className="space-y-3">
                            <div className="flex items-center gap-3">
                                <span className={`px-4 py-2 rounded-lg text-sm font-medium border ${getStatusColor(student.status)}`}>
                                    {student.status || 'ABSENT'}
                                </span>
                            </div>
                            <div className="flex gap-2">
                                <button
                                    onClick={() => handleStatusChange('ONBUS')}
                                    className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                                        student.status === 'ONBUS'
                                            ? 'bg-green-600 text-white shadow-md'
                                            : 'bg-green-100 text-green-700 hover:bg-green-200'
                                    }`}
                                >
                                    On Bus
                                </button>
                                <button
                                    onClick={() => handleStatusChange('DROPPEDOFF')}
                                    className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                                        student.status === 'DROPPEDOFF'
                                            ? 'bg-emerald-600 text-white shadow-md'
                                            : 'bg-emerald-100 text-emerald-700 hover:bg-emerald-200'
                                    }`}
                                >
                                    Dropped Off
                                </button>
                                <button
                                    onClick={() => handleStatusChange('ABSENT')}
                                    className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                                        student.status === 'ABSENT'
                                            ? 'bg-red-600 text-white shadow-md'
                                            : 'bg-red-100 text-red-700 hover:bg-red-200'
                                    }`}
                                >
                                    Absent
                                </button>
                            </div>
                            <p className="text-xs text-gray-500 mt-2">
                                Update your status to indicate your current location or availability
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StudentProfile;
