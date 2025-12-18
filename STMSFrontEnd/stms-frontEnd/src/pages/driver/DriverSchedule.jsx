import React, { useState, useEffect } from 'react';
import { Calendar, MapPin, Clock } from 'lucide-react';
import { scheduleService } from '../../services/scheduleService';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const DriverSchedule = () => {
    const { user } = useAuth();
    const [schedules, setSchedules] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (user?.email) {
            fetchSchedules();
        }
    }, [user]);

    const fetchSchedules = async () => {
        setLoading(true);
        try {
            const schedulesData = await scheduleService.getByDriverEmail(user.email);
            setSchedules(schedulesData || []);
        } catch (error) {
            console.error('Error fetching schedules:', error);
            toast.error('Failed to load schedules');
        } finally {
            setLoading(false);
        }
    };

    const formatTime = (timeString) => {
        if (!timeString) return 'N/A';
        try {
            const time = new Date(`2000-01-01T${timeString}`);
            return time.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
        } catch {
            return timeString;
        }
    };

    const daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
    const dayLabels = {
        'MONDAY': 'Monday',
        'TUESDAY': 'Tuesday',
        'WEDNESDAY': 'Wednesday',
        'THURSDAY': 'Thursday',
        'FRIDAY': 'Friday',
        'SATURDAY': 'Saturday',
        'SUNDAY': 'Sunday'
    };

    return (
        <div>
            <div className="mb-6">
                <h1 className="text-3xl font-bold text-gray-900">My Schedule</h1>
                <p className="text-gray-600 mt-1">View your weekly schedule and assignments</p>
            </div>

            {loading ? (
                <div className="bg-white rounded-xl shadow-md p-8 text-center">
                    <div className="text-gray-500">Loading schedule...</div>
                </div>
            ) : schedules.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {daysOfWeek.map((day) => {
                        const daySchedule = schedules.find(s => s.dayOfWeek?.toUpperCase() === day);
                        return (
                            <div key={day} className="bg-white rounded-xl shadow-md overflow-hidden">
                                <div className="bg-emerald-700 p-4 text-white">
                                    <h3 className="text-lg font-bold">{dayLabels[day] || day}</h3>
                                </div>
                                <div className="p-6">
                                    {daySchedule ? (
                                        <div className="space-y-4">
                                            {daySchedule.route && (
                                                <div className="flex items-start gap-3">
                                                    <div className="bg-emerald-100 p-2 rounded-lg">
                                                        <MapPin className="h-5 w-5 text-emerald-700" />
                                                    </div>
                                                    <div className="flex-1">
                                                        <p className="text-sm text-gray-500">Route</p>
                                                        <p className="font-semibold text-gray-900">
                                                            {daySchedule.route.name || 'N/A'}
                                                        </p>
                                                    </div>
                                                </div>
                                            )}
                                            <div className="grid grid-cols-2 gap-3">
                                                <div>
                                                    <p className="text-xs text-gray-500">Departure</p>
                                                    <div className="flex items-center gap-1 mt-1">
                                                        <Clock className="h-4 w-4 text-gray-400" />
                                                        <p className="text-sm font-medium text-gray-900">
                                                            {formatTime(daySchedule.departureTime)}
                                                        </p>
                                                    </div>
                                                </div>
                                                <div>
                                                    <p className="text-xs text-gray-500">Arrival</p>
                                                    <div className="flex items-center gap-1 mt-1">
                                                        <Clock className="h-4 w-4 text-gray-400" />
                                                        <p className="text-sm font-medium text-gray-900">
                                                            {formatTime(daySchedule.arrivalTime)}
                                                        </p>
                                                    </div>
                                                </div>
                                            </div>
                                            {daySchedule.bus && (
                                                <div className="pt-3 border-t border-gray-200">
                                                    <p className="text-xs text-gray-500">Bus</p>
                                                    <p className="text-sm font-semibold text-gray-900">
                                                        {daySchedule.bus.plateNumber || 'N/A'}
                                                    </p>
                                                </div>
                                            )}
                                        </div>
                                    ) : (
                                        <div className="text-center py-4 text-gray-400">
                                            <p className="text-sm">No schedule</p>
                                        </div>
                                    )}
                                </div>
                            </div>
                        );
                    })}
                </div>
            ) : (
                <div className="bg-white rounded-xl shadow-md p-12 text-center">
                    <Calendar className="h-16 w-16 mx-auto mb-4 text-gray-400" />
                    <p className="text-xl font-medium text-gray-900">No schedule assigned</p>
                    <p className="text-sm text-gray-500 mt-2">Your schedule will appear here once assigned by an administrator</p>
                </div>
            )}
        </div>
    );
};

export default DriverSchedule;
