import React, { useState, useEffect } from 'react';
import { Bell, Calendar } from 'lucide-react';
import { studentService } from '../../services/studentService';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const StudentDashboard = () => {
    const { user } = useAuth();
    const [statistics, setStatistics] = useState({
        announcementCount: 0,
        scheduleCount: 0
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (user?.email) {
            fetchStatistics();
        }
    }, [user]);

    const fetchStatistics = async () => {
        setLoading(true);
        try {
            const stats = await studentService.getStatistics(user.email);
            setStatistics(stats || {
                announcementCount: 0,
                scheduleCount: 0
            });
        } catch (error) {
            console.error('Error fetching statistics:', error);
            toast.error('Failed to load statistics');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="space-y-6">
            {/* Statistics Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-white rounded-xl shadow-md p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600 mb-1">Announcements</p>
                            <p className="text-3xl font-bold text-gray-900">{statistics.announcementCount || 0}</p>
                        </div>
                        <div className="bg-emerald-100 p-3 rounded-lg">
                            <Bell className="h-8 w-8 text-emerald-700" />
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-xl shadow-md p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-600 mb-1">Schedules</p>
                            <p className="text-3xl font-bold text-gray-900">{statistics.scheduleCount || 0}</p>
                        </div>
                        <div className="bg-emerald-100 p-3 rounded-lg">
                            <Calendar className="h-8 w-8 text-emerald-700" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StudentDashboard;
