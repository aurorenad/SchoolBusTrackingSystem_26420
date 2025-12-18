import React, { useState, useEffect } from 'react';
import { Bell } from 'lucide-react';
import { announcementService } from '../../services/announcementService';
import toast from 'react-hot-toast';

const StudentAnnouncements = () => {
    const [announcements, setAnnouncements] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchAnnouncements();
    }, []);

    const fetchAnnouncements = async () => {
        setLoading(true);
        try {
            const announcementsData = await announcementService.getForStudents();
            // Sort by createdAt descending (newest first)
            const sorted = (announcementsData || []).sort((a, b) => {
                const dateA = new Date(a.createdAt || 0);
                const dateB = new Date(b.createdAt || 0);
                return dateB - dateA;
            });
            setAnnouncements(sorted);
        } catch (error) {
            console.error('Error fetching announcements:', error);
            toast.error('Failed to load announcements');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <div className="mb-6">
                <h1 className="text-3xl font-bold text-gray-900">Announcements</h1>
                <p className="text-gray-600 mt-1">View important updates and announcements</p>
            </div>

            <div className="bg-white rounded-xl shadow-md overflow-hidden">
                <div className="bg-emerald-700 p-6 text-white">
                    <div className="flex items-center gap-2">
                        <Bell className="h-6 w-6" />
                        <h2 className="text-2xl font-bold">All Announcements</h2>
                        {announcements.length > 0 && (
                            <span className="bg-white/20 px-3 py-1 rounded-full text-sm">
                                {announcements.length}
                            </span>
                        )}
                    </div>
                </div>
                <div className="p-6">
                    {loading ? (
                        <div className="text-center py-8 text-gray-500">Loading...</div>
                    ) : announcements.length > 0 ? (
                        <div className="space-y-4">
                            {announcements.map((announcement) => (
                                <div key={announcement.id} className="border border-gray-200 rounded-lg p-6 hover:bg-gray-50 transition">
                                    <div className="flex items-start justify-between mb-3">
                                        <h3 className="text-xl font-semibold text-gray-900">
                                            {announcement.title}
                                        </h3>
                                        {announcement.createdAt && (
                                            <span className="text-sm text-gray-500 whitespace-nowrap ml-4">
                                                {new Date(announcement.createdAt).toLocaleDateString('en-US', {
                                                    year: 'numeric',
                                                    month: 'long',
                                                    day: 'numeric'
                                                })}
                                            </span>
                                        )}
                                    </div>
                                    <p className="text-gray-700 whitespace-pre-wrap leading-relaxed">
                                        {announcement.message}
                                    </p>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-12 text-gray-500">
                            <Bell className="h-16 w-16 mx-auto mb-4 text-gray-400" />
                            <p className="text-xl font-medium">No announcements</p>
                            <p className="text-sm mt-2">You'll see important updates here when they're posted</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default StudentAnnouncements;
