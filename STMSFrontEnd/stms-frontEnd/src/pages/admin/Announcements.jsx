import React, { useState, useEffect } from 'react';
import { Plus, Search, Bell, Megaphone } from 'lucide-react';
import { announcementService } from '../../services/announcementService';
import toast from 'react-hot-toast';

const getErrorMessage = (error, fallback = 'Operation failed') => {
    const data = error?.response?.data;
    if (!data) return error?.message || fallback;
    if (typeof data === 'string') return data;
    return data?.message || data?.error || (() => {
        try {
            return JSON.stringify(data);
        } catch {
            return fallback;
        }
    })();
};

const Announcements = () => {
    const [announcements, setAnnouncements] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        title: '',
        message: '',
        priority: 'NORMAL',
        targetAudience: 'ALL'
    });

    useEffect(() => {
        fetchAnnouncements();
    }, []);

    const fetchAnnouncements = async () => {
        try {
            const data = await announcementService.getAll();
            setAnnouncements(data || []);
        } catch (error) {
            toast.error('Failed to fetch announcements');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await announcementService.create(formData);
            toast.success('Announcement created successfully');
            setShowModal(false);
            setFormData({ title: '', message: '', priority: 'NORMAL', targetAudience: 'ALL' });
            fetchAnnouncements();
        } catch (error) {
            toast.error(getErrorMessage(error, 'Operation failed'));
        }
    };

    const filteredAnnouncements = announcements.filter(announcement =>
        announcement.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        announcement.message?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getPriorityColor = (priority) => {
        switch (priority) {
            case 'HIGH': return 'bg-red-100 text-red-700';
            case 'URGENT': return 'bg-orange-100 text-orange-700';
            default: return 'bg-blue-100 text-blue-700';
        }
    };

    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Announcements</h1>
                    <p className="text-gray-600 mt-1">Manage system announcements</p>
                </div>
                <button
                    onClick={() => { setShowModal(true); setFormData({ title: '', message: '', priority: 'NORMAL', targetAudience: 'ALL' }); }}
                    className="flex items-center gap-2 bg-emerald-700 text-white px-4 py-2 rounded-lg hover:bg-emerald-800 transition"
                >
                    <Plus className="h-5 w-5" /> New Announcement
                </button>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <div className="flex items-center gap-4 mb-6">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                        <input
                            type="text"
                            placeholder="Search announcements..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                        />
                    </div>
                </div>

                {loading ? (
                    <div className="text-center py-8 text-gray-500">Loading...</div>
                ) : filteredAnnouncements.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">No announcements found</div>
                ) : (
                    <div className="space-y-4">
                        {filteredAnnouncements.map((announcement, index) => (
                            <div key={announcement.announcementId || index} className="p-4 border border-gray-100 rounded-lg hover:bg-gray-50 transition">
                                <div className="flex items-start gap-4">
                                    <div className="h-12 w-12 bg-amber-100 rounded-lg flex items-center justify-center flex-shrink-0">
                                        <Megaphone className="h-6 w-6 text-amber-700" />
                                    </div>
                                    <div className="flex-1">
                                        <div className="flex items-center gap-3 mb-2">
                                            <h3 className="font-semibold text-gray-900">{announcement.title}</h3>
                                            <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${getPriorityColor(announcement.priority)}`}>
                                                {announcement.priority || 'NORMAL'}
                                            </span>
                                        </div>
                                        <p className="text-gray-600 text-sm">{announcement.message}</p>
                                        <p className="text-gray-400 text-xs mt-2">
                                            {announcement.createdAt ? new Date(announcement.createdAt).toLocaleDateString() : 'Recently'}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {showModal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-xl p-6 w-full max-w-md">
                        <h2 className="text-xl font-bold mb-4">New Announcement</h2>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
                                <input
                                    type="text"
                                    value={formData.title}
                                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Message</label>
                                <textarea
                                    value={formData.message}
                                    onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                                    rows={4}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Priority</label>
                                <select
                                    value={formData.priority}
                                    onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                >
                                    <option value="NORMAL">Normal</option>
                                    <option value="HIGH">High</option>
                                    <option value="URGENT">Urgent</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Target Audience</label>
                                <select
                                    value={formData.targetAudience}
                                    onChange={(e) => setFormData({ ...formData, targetAudience: e.target.value })}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    required
                                >
                                    <option value="ALL">All</option>
                                    <option value="DRIVER">Drivers Only</option>
                                    <option value="STUDENT">Students Only</option>
                                </select>
                            </div>
                            <div className="flex gap-3 pt-4">
                                <button
                                    type="button"
                                    onClick={() => setShowModal(false)}
                                    className="flex-1 px-4 py-2 border border-gray-200 rounded-lg hover:bg-gray-50"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 px-4 py-2 bg-emerald-700 text-white rounded-lg hover:bg-emerald-800"
                                >
                                    Publish
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Announcements;
