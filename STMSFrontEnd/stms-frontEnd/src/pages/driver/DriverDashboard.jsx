import React, { useState, useEffect } from 'react';
import { MapPin, Users, Bus, Activity } from 'lucide-react';
import { driverService } from '../../services/driverService';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const DriverDashboard = () => {
    const { user } = useAuth();
    const [statistics, setStatistics] = useState({
        busCount: 0,
        studentCount: 0,
        pickupPointCount: 0,
        recentActivities: []
    });
    const [loading, setLoading] = useState(true);


    useEffect(() => {
        fetchData();
    }, [user]);

    const fetchData = async () => {
        if (!user?.email) return;
        
        setLoading(true);
        try {
            // Fetch driver statistics
            try {
                console.log('Fetching statistics for driver email:', user.email);
                const stats = await driverService.getStatistics(user.email);
                console.log('Driver statistics received:', stats);
                
                if (stats.busCount === 0) {
                    console.warn('No bus found for driver. Make sure:');
                    console.warn('1. A bus is assigned to this driver');
                    console.warn('2. The driver email matches the logged-in user email');
                    console.warn('3. Check backend logs for DEBUG messages');
                }
                
                setStatistics(stats || {
                    busCount: 0,
                    studentCount: 0,
                    pickupPointCount: 0,
                    recentActivities: []
                });
            } catch (error) {
                console.error('Error fetching statistics:', error);
                console.error('Error details:', error.response?.data || error.message);
                toast.error('Failed to load statistics. Check console for details.');
            }
        } catch (error) {
            console.error('Error fetching dashboard data:', error);
            toast.error('Failed to load dashboard data');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="space-y-6">
                {/* Statistics Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div className="bg-white rounded-xl shadow-md p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm text-gray-600 mb-1">Buses Assigned</p>
                                <p className="text-3xl font-bold text-gray-900">{statistics.busCount || 0}</p>
                            </div>
                            <div className="bg-emerald-100 p-3 rounded-lg">
                                <Bus className="h-8 w-8 text-emerald-700" />
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-xl shadow-md p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm text-gray-600 mb-1">Students to Pick Up</p>
                                <p className="text-3xl font-bold text-gray-900">{statistics.studentCount || 0}</p>
                            </div>
                            <div className="bg-blue-100 p-3 rounded-lg">
                                <Users className="h-8 w-8 text-blue-700" />
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-xl shadow-md p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm text-gray-600 mb-1">Pickup Points</p>
                                <p className="text-3xl font-bold text-gray-900">{statistics.pickupPointCount || 0}</p>
                            </div>
                            <div className="bg-purple-100 p-3 rounded-lg">
                                <MapPin className="h-8 w-8 text-purple-700" />
                            </div>
                        </div>
                    </div>
                </div>

                {/* Recent Activities */}
                <div className="bg-white rounded-xl shadow-md overflow-hidden">
                    <div className="bg-emerald-700 p-6 text-white">
                        <div className="flex items-center gap-2">
                            <Activity className="h-6 w-6" />
                            <h2 className="text-2xl font-bold">Recent Activities</h2>
                        </div>
                    </div>
                    <div className="p-6">
                        {loading ? (
                            <div className="text-center py-4 text-gray-500">Loading...</div>
                        ) : statistics.recentActivities && statistics.recentActivities.length > 0 ? (
                            <div className="space-y-3">
                                {statistics.recentActivities.map((activity, index) => (
                                    <div key={index} className="flex items-start gap-3 p-3 border border-gray-200 rounded-lg hover:bg-gray-50">
                                        <div className="bg-emerald-100 p-2 rounded-lg mt-0.5">
                                            <Activity className="h-4 w-4 text-emerald-700" />
                                        </div>
                                        <div className="flex-1">
                                            <p className="text-sm font-medium text-gray-900">{activity.type || 'Activity'}</p>
                                            <p className="text-sm text-gray-600 mt-1">{activity.description || 'No description'}</p>
                                            {activity.time && (
                                                <p className="text-xs text-gray-400 mt-1">
                                                    {new Date(activity.time).toLocaleString()}
                                                </p>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="text-center py-8 text-gray-500">
                                <Activity className="h-12 w-12 mx-auto mb-3 text-gray-400" />
                                <p className="text-lg font-medium">No recent activities</p>
                                <p className="text-sm mt-1">Your activities will appear here</p>
                            </div>
                        )}
                    </div>
                </div>
        </div>
    );
};

export default DriverDashboard;
