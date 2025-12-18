import React, { useEffect, useMemo, useState } from 'react';
import { Plus, Search, Edit2, Trash2, MapPin } from 'lucide-react';
import toast from 'react-hot-toast';
import { locationService } from '../../services/locationService';

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

const LOCATION_TYPES = ['PROVINCE', 'DISTRICT', 'SECTOR', 'CELL', 'VILLAGE'];

const Locations = () => {
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [type, setType] = useState('PROVINCE');
    const [locations, setLocations] = useState([]);

    const [showModal, setShowModal] = useState(false);
    const [editing, setEditing] = useState(null);

    const [parentCode, setParentCode] = useState('');
    const [parentOptions, setParentOptions] = useState([]);

    const [formData, setFormData] = useState({
        name: '',
        code: '',
        type: 'PROVINCE'
    });

    const fetchLocations = async () => {
        setLoading(true);
        try {
            const page = await locationService.getByType(type, { page: 0, size: 200 });
            setLocations(page?.content || []);
        } catch (e) {
            toast.error(getErrorMessage(e, 'Failed to fetch locations'));
        } finally {
            setLoading(false);
        }
    };

    const loadParentOptions = async (targetType) => {
        // parent type is one level above
        const parentTypeMap = {
            DISTRICT: 'PROVINCE',
            SECTOR: 'DISTRICT',
            CELL: 'SECTOR',
            VILLAGE: 'CELL'
        };
        const parentType = parentTypeMap[targetType];
        if (!parentType) {
            setParentOptions([]);
            return;
        }

        try {
            const page = await locationService.getByType(parentType, { page: 0, size: 500 });
            setParentOptions(page?.content || []);
        } catch (e) {
            setParentOptions([]);
        }
    };

    useEffect(() => {
        fetchLocations();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [type]);

    useEffect(() => {
        // keep modal form type in sync with selected type
        setFormData((p) => ({ ...p, type }));
        setParentCode('');
        loadParentOptions(type);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [type]);

    const filtered = useMemo(() => {
        const q = searchTerm.trim().toLowerCase();
        if (!q) return locations;
        return locations.filter((l) =>
            l.name?.toLowerCase().includes(q) ||
            l.code?.toLowerCase().includes(q)
        );
    }, [locations, searchTerm]);

    const openAdd = () => {
        setEditing(null);
        setFormData({ name: '', code: '', type });
        setParentCode('');
        setShowModal(true);
    };

    const openEdit = (loc) => {
        setEditing(loc);
        setFormData({ name: loc.name || '', code: loc.code || '', type: loc.type || type });
        setParentCode(loc.parent?.code || '');
        setShowModal(true);
        loadParentOptions(loc.type || type);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editing) {
                await locationService.update(editing.code, {
                    name: formData.name,
                    code: formData.code,
                    type: formData.type
                });
                toast.success('Location updated');
            } else {
                const payload = { name: formData.name, code: formData.code, type: formData.type };
                if (formData.type === 'PROVINCE') {
                    await locationService.createProvince(payload);
                } else {
                    if (!parentCode) {
                        toast.error('Parent is required for this type');
                        return;
                    }
                    await locationService.createChild(parentCode, payload);
                }
                toast.success('Location saved');
            }
            setShowModal(false);
            fetchLocations();
        } catch (e) {
            toast.error(getErrorMessage(e, 'Operation failed'));
        }
    };

    const handleDelete = async (code) => {
        if (!confirm('Delete this location?')) return;
        try {
            await locationService.delete(code);
            toast.success('Location deleted');
            fetchLocations();
        } catch (e) {
            toast.error(getErrorMessage(e, 'Failed to delete location'));
        }
    };

    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Locations</h1>
                    <p className="text-gray-600 mt-1">Manage province → village hierarchy</p>
                </div>
                <button
                    onClick={openAdd}
                    className="flex items-center gap-2 bg-emerald-700 text-white px-4 py-2 rounded-lg hover:bg-emerald-800 transition"
                >
                    <Plus className="h-5 w-5" /> Add Location
                </button>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <div className="flex items-center gap-4 mb-6">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                        <input
                            type="text"
                            placeholder="Search by name or code..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                        />
                    </div>

                    <select
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                        className="px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    >
                        {LOCATION_TYPES.map((t) => (
                            <option key={t} value={t}>{t}</option>
                        ))}
                    </select>
                </div>

                {loading ? (
                    <div className="text-center py-8 text-gray-500">Loading...</div>
                ) : filtered.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">No locations found</div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead>
                                <tr className="border-b border-gray-200">
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Name</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Code</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Type</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Parent</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filtered.map((loc) => (
                                    <tr key={loc.code} className="border-b border-gray-100 hover:bg-gray-50">
                                        <td className="py-3 px-4 flex items-center gap-3">
                                            <div className="h-10 w-10 bg-teal-100 rounded-lg flex items-center justify-center">
                                                <MapPin className="h-5 w-5 text-teal-700" />
                                            </div>
                                            {loc.name}
                                        </td>
                                        <td className="py-3 px-4">{loc.code}</td>
                                        <td className="py-3 px-4">{loc.type}</td>
                                        <td className="py-3 px-4">{loc.parent?.name || '-'}</td>
                                        <td className="py-3 px-4">
                                            <div className="flex items-center gap-2">
                                                <button onClick={() => openEdit(loc)} className="p-2 hover:bg-gray-100 rounded-lg">
                                                    <Edit2 className="h-4 w-4 text-gray-600" />
                                                </button>
                                                <button onClick={() => handleDelete(loc.code)} className="p-2 hover:bg-red-50 rounded-lg">
                                                    <Trash2 className="h-4 w-4 text-red-600" />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {showModal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-xl p-6 w-full max-w-md">
                        <h2 className="text-xl font-bold mb-4">{editing ? 'Edit Location' : 'Add Location'}</h2>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
                                <select
                                    value={formData.type}
                                    onChange={(e) => {
                                        const nextType = e.target.value;
                                        setFormData((p) => ({ ...p, type: nextType }));
                                        setParentCode('');
                                        loadParentOptions(nextType);
                                    }}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                >
                                    {LOCATION_TYPES.map((t) => (
                                        <option key={t} value={t}>{t}</option>
                                    ))}
                                </select>
                            </div>

                            {formData.type !== 'PROVINCE' && (
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Parent</label>
                                    <select
                                        value={parentCode}
                                        onChange={(e) => setParentCode(e.target.value)}
                                        className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    >
                                        <option value="">Select parent</option>
                                        {parentOptions.map((p) => (
                                            <option key={p.code} value={p.code}>{p.name} ({p.code})</option>
                                        ))}
                                    </select>
                                </div>
                            )}

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                                <input
                                    type="text"
                                    value={formData.name}
                                    onChange={(e) => setFormData((p) => ({ ...p, name: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Code</label>
                                <input
                                    type="text"
                                    value={formData.code}
                                    onChange={(e) => setFormData((p) => ({ ...p, code: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    required
                                />
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
                                    {editing ? 'Update' : 'Save'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Locations;


