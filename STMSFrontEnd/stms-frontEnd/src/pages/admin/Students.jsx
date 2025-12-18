import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit2, Trash2, GraduationCap, Bus as BusIcon } from 'lucide-react';
import { studentService } from '../../services/studentService';
import { busService } from '../../services/busService';
import { locationService } from '../../services/locationService';
import toast from 'react-hot-toast';

const getErrorMessage = (error, fallback = 'Operation failed') => {
    const data = error?.response?.data;
    if (!data) return error?.message || fallback;
    if (typeof data === 'string') return data;
    // Spring Boot default error shape: {timestamp,status,error,path}
    return data?.message || data?.error || (() => {
        try {
            return JSON.stringify(data);
        } catch {
            return fallback;
        }
    })();
};

const Students = () => {
    const [students, setStudents] = useState([]);
    const [buses, setBuses] = useState([]);

    const [provinces, setProvinces] = useState([]);
    const [districts, setDistricts] = useState([]);
    const [sectors, setSectors] = useState([]);
    const [cells, setCells] = useState([]);
    const [villages, setVillages] = useState([]);

    const [provinceCode, setProvinceCode] = useState('');
    const [districtCode, setDistrictCode] = useState('');
    const [sectorCode, setSectorCode] = useState('');
    const [cellCode, setCellCode] = useState('');
    const [villageCode, setVillageCode] = useState('');

    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editingStudent, setEditingStudent] = useState(null);
    const [showAssignBusModal, setShowAssignBusModal] = useState(false);
    const [assigningStudent, setAssigningStudent] = useState(null);
    const [selectedBusId, setSelectedBusId] = useState('');
    const [formPage, setFormPage] = useState(1);
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        className: '',
        pickUpPoint: '',
        dropOffPoint: '',
        status: 'ABSENT'
    });

    useEffect(() => {
        fetchStudents();
        fetchBuses();
        fetchProvinces();
    }, []);

    const fetchStudents = async () => {
        try {
            const data = await studentService.getAll();
            setStudents(data || []);
        } catch (error) {
            toast.error('Failed to fetch students');
        } finally {
            setLoading(false);
        }
    };

    const fetchBuses = async () => {
        try {
            const data = await busService.getAll();
            setBuses(data || []);
        } catch (error) {
            console.error('Failed to fetch buses');
        }
    };

    const fetchProvinces = async () => {
        try {
            const data = await locationService.getRoots();
            setProvinces((data || []).filter((l) => l.type === 'PROVINCE'));
            if (!data || data.length === 0) {
                toast.error('No provinces found. Add locations in Admin → Locations.');
            }
        } catch (error) {
            toast.error(getErrorMessage(error, 'Failed to fetch provinces'));
        }
    };

    const resetLocationBelowProvince = () => {
        setDistricts([]);
        setSectors([]);
        setCells([]);
        setVillages([]);
        setDistrictCode('');
        setSectorCode('');
        setCellCode('');
        setVillageCode('');
    };

    const resetLocationBelowDistrict = () => {
        setSectors([]);
        setCells([]);
        setVillages([]);
        setSectorCode('');
        setCellCode('');
        setVillageCode('');
    };

    const resetLocationBelowSector = () => {
        setCells([]);
        setVillages([]);
        setCellCode('');
        setVillageCode('');
    };

    const resetLocationBelowCell = () => {
        setVillages([]);
        setVillageCode('');
    };

    const handleProvinceChange = async (code) => {
        setProvinceCode(code);
        resetLocationBelowProvince();
        if (!code) return;
        try {
            const kids = await locationService.getChildren(code);
            setDistricts((kids || []).filter((l) => l.type === 'DISTRICT'));
        } catch (e) {
            toast.error('Failed to load districts');
        }
    };

    const handleDistrictChange = async (code) => {
        setDistrictCode(code);
        resetLocationBelowDistrict();
        if (!code) return;
        try {
            const kids = await locationService.getChildren(code);
            setSectors((kids || []).filter((l) => l.type === 'SECTOR'));
        } catch (e) {
            toast.error('Failed to load sectors');
        }
    };

    const handleSectorChange = async (code) => {
        setSectorCode(code);
        resetLocationBelowSector();
        if (!code) return;
        try {
            const kids = await locationService.getChildren(code);
            setCells((kids || []).filter((l) => l.type === 'CELL'));
        } catch (e) {
            toast.error('Failed to load cells');
        }
    };

    const handleCellChange = async (code) => {
        setCellCode(code);
        resetLocationBelowCell();
        if (!code) return;
        try {
            const kids = await locationService.getChildren(code);
            setVillages((kids || []).filter((l) => l.type === 'VILLAGE'));
        } catch (e) {
            toast.error('Failed to load villages');
        }
    };

    const hydrateLocationForEdit = async (anyCode) => {
        if (!anyCode) return;
        try {
            const codesByType = {};
            let currentCode = anyCode;
            let guard = 0;

            while (currentCode && guard < 10) {
                const loc = await locationService.getByCode(currentCode);
                if (!loc) break;
                if (loc.type && loc.code) {
                    codesByType[loc.type] = loc.code;
                }
                currentCode = loc.parent?.code || '';
                guard += 1;
            }

            const p = codesByType.PROVINCE || '';
            const d = codesByType.DISTRICT || '';
            const s = codesByType.SECTOR || '';
            const c = codesByType.CELL || '';
            const v = codesByType.VILLAGE || anyCode;

            setProvinceCode(p);
            if (p) {
                const dKids = await locationService.getChildren(p);
                setDistricts((dKids || []).filter((l) => l.type === 'DISTRICT'));
            }

            setDistrictCode(d);
            if (d) {
                const sKids = await locationService.getChildren(d);
                setSectors((sKids || []).filter((l) => l.type === 'SECTOR'));
            }

            setSectorCode(s);
            if (s) {
                const cKids = await locationService.getChildren(s);
                setCells((cKids || []).filter((l) => l.type === 'CELL'));
            }

            setCellCode(c);
            if (c) {
                const vKids = await locationService.getChildren(c);
                setVillages((vKids || []).filter((l) => l.type === 'VILLAGE'));
            }

            setVillageCode(v);
        } catch (e) {
            // ignore; editing should still work
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (!villageCode) {
                toast.error('Student location (village) is required');
                return;
            }

            const payload = {
                ...formData,
                location: { code: villageCode }
            };

            if (editingStudent) {
                await studentService.update(editingStudent.id, payload);
                toast.success('Student updated successfully');
            } else {
                await studentService.create(payload);
                toast.success('Student added successfully');
                toast('A password setup OTP was sent to the student email.');
            }
            setShowModal(false);
            setEditingStudent(null);
            setFormPage(1);
            setFormData({ name: '', email: '', className: '', pickUpPoint: '', dropOffPoint: '', status: 'ABSENT' });
            setProvinceCode('');
            resetLocationBelowProvince();
            fetchStudents();
        } catch (error) {
            toast.error(getErrorMessage(error, 'Operation failed'));
        }
    };

    const handleEdit = (student) => {
        setEditingStudent(student);
        setFormPage(1);
        setFormData({
            name: student.name || '',
            email: student.email || '',
            className: student.className || '',
            pickUpPoint: student.pickUpPoint || '',
            dropOffPoint: student.dropOffPoint || '',
            status: student.status || 'ABSENT'
        });
        setProvinceCode('');
        resetLocationBelowProvince();
        hydrateLocationForEdit(student.location?.code);
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (!confirm('Are you sure you want to delete this student?')) return;
        try {
            await studentService.delete(id);
            toast.success('Student deleted successfully');
            fetchStudents();
        } catch (error) {
            toast.error('Failed to delete student');
        }
    };

    const openAssignBus = (student) => {
        setAssigningStudent(student);
        setSelectedBusId(student?.bus?.id ? String(student.bus.id) : '');
        setShowAssignBusModal(true);
    };

    const handleAssignBus = async (e) => {
        e.preventDefault();
        if (!assigningStudent?.id) return;
        if (!selectedBusId) {
            toast.error('Please select a bus');
            return;
        }
        try {
            await studentService.assignBus(assigningStudent.id, Number(selectedBusId));
            toast.success('Bus assigned successfully');
            setShowAssignBusModal(false);
            setAssigningStudent(null);
            setSelectedBusId('');
            fetchStudents();
        } catch (error) {
            toast.error(getErrorMessage(error, 'Failed to assign bus'));
        }
    };

    const filteredStudents = students.filter(student =>
        student.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        student.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        student.className?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div>
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Students</h1>
                    <p className="text-gray-600 mt-1">Manage student records</p>
                </div>
                <button
                    onClick={() => {
                        setShowModal(true);
                        setEditingStudent(null);
                        setFormPage(1);
                        setFormData({ name: '', email: '', className: '', pickUpPoint: '', dropOffPoint: '', status: 'ABSENT' });
                        setProvinceCode('');
                        resetLocationBelowProvince();
                    }}
                    className="flex items-center gap-2 bg-emerald-700 text-white px-4 py-2 rounded-lg hover:bg-emerald-800 transition"
                >
                    <Plus className="h-5 w-5" /> Add Student
                </button>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <div className="flex items-center gap-4 mb-6">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                        <input
                            type="text"
                            placeholder="Search students..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                        />
                    </div>
                </div>

                {loading ? (
                    <div className="text-center py-8 text-gray-500">Loading...</div>
                ) : filteredStudents.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">No students found</div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead>
                                <tr className="border-b border-gray-200">
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Name</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Email</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Class</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Pick Up</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Drop Off</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Location</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Bus</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Status</th>
                                    <th className="text-left py-3 px-4 text-gray-600 font-semibold">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredStudents.map((student) => (
                                    <tr key={student.id} className="border-b border-gray-100 hover:bg-gray-50">
                                        <td className="py-3 px-4 flex items-center gap-3">
                                            <div className="h-10 w-10 bg-lime-100 rounded-lg flex items-center justify-center">
                                                <GraduationCap className="h-5 w-5 text-lime-700" />
                                            </div>
                                            {student.name}
                                        </td>
                                        <td className="py-3 px-4">{student.email || '-'}</td>
                                        <td className="py-3 px-4">{student.className || '-'}</td>
                                        <td className="py-3 px-4">{student.pickUpPoint || '-'}</td>
                                        <td className="py-3 px-4">{student.dropOffPoint || '-'}</td>
                                        <td className="py-3 px-4">{student.location?.name || '-'}</td>
                                        <td className="py-3 px-4">{student.bus?.plateNumber || '-'}</td>
                                        <td className="py-3 px-4">
                                            <span className={`px-2 py-1 rounded-full text-xs font-medium ${student.status === 'ONBUS' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                                                }`}>
                                                {student.status || '-'}
                                            </span>
                                        </td>
                                        <td className="py-3 px-4">
                                            <div className="flex items-center gap-2">
                                                <button
                                                    onClick={() => openAssignBus(student)}
                                                    className="p-2 hover:bg-emerald-50 rounded-lg"
                                                    title="Assign bus"
                                                >
                                                    <BusIcon className="h-4 w-4 text-emerald-700" />
                                                </button>
                                                <button onClick={() => handleEdit(student)} className="p-2 hover:bg-gray-100 rounded-lg">
                                                    <Edit2 className="h-4 w-4 text-gray-600" />
                                                </button>
                                                <button onClick={() => handleDelete(student.id)} className="p-2 hover:bg-red-50 rounded-lg">
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

            {showAssignBusModal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-xl p-6 w-full max-w-md">
                        <h2 className="text-xl font-bold mb-4">Assign Bus</h2>
                        <p className="text-sm text-gray-600 mb-4">
                            Student: <span className="font-medium">{assigningStudent?.name}</span>
                        </p>
                        <form onSubmit={handleAssignBus} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Bus</label>
                                <select
                                    value={selectedBusId}
                                    onChange={(e) => setSelectedBusId(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    required
                                >
                                    <option value="">Select bus</option>
                                    {buses.map((b) => (
                                        <option key={b.id} value={b.id}>
                                            {b.plateNumber} (cap: {b.capacity})
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="flex gap-3 pt-2">
                                <button
                                    type="button"
                                    onClick={() => { setShowAssignBusModal(false); setAssigningStudent(null); setSelectedBusId(''); }}
                                    className="flex-1 px-4 py-2 border border-gray-200 rounded-lg hover:bg-gray-50"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="flex-1 px-4 py-2 bg-emerald-700 text-white rounded-lg hover:bg-emerald-800"
                                >
                                    Assign
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {showModal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 overflow-y-auto">
                    <div className="bg-white rounded-xl p-6 w-full max-w-4xl my-8 max-h-[90vh] overflow-y-auto">
                        <div className="flex items-center justify-between mb-6">
                            <h2 className="text-xl font-bold">{editingStudent ? 'Edit Student' : 'Add New Student'}</h2>
                            <div className="flex items-center gap-2">
                                <span className="text-sm text-gray-500">Page {formPage} of 3</span>
                                <div className="flex gap-1">
                                    {[1, 2, 3].map((page) => (
                                        <button
                                            key={page}
                                            type="button"
                                            onClick={() => setFormPage(page)}
                                            className={`w-2 h-2 rounded-full transition ${
                                                formPage === page ? 'bg-emerald-700' : 'bg-gray-300'
                                            }`}
                                        />
                                    ))}
                                </div>
                            </div>
                        </div>
                        <form onSubmit={handleSubmit}>
                            {/* Page 1: Basic Information */}
                            {formPage === 1 && (
                            <div className="mb-6">
                                <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-2 border-b border-gray-200">Basic Information</h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Student Name *</label>
                                        <input
                                            type="text"
                                            value={formData.name}
                                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            required
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Email *</label>
                                        <input
                                            type="email"
                                            value={formData.email}
                                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            required
                                            disabled={!!editingStudent}
                                        />
                                        <p className="text-xs text-gray-500 mt-1">An OTP will be sent for password setup.</p>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Class *</label>
                                        <input
                                            type="text"
                                            value={formData.className}
                                            onChange={(e) => setFormData({ ...formData, className: e.target.value })}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            required
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
                                        <select
                                            value={formData.status}
                                            onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                        >
                                            <option value="ONBUS">On Bus</option>
                                            <option value="DROPPEDOFF">Dropped Off</option>
                                            <option value="ABSENT">Absent</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            )}

                            {/* Page 2: Transport Points */}
                            {formPage === 2 && (
                            <div className="mb-6">
                                <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-2 border-b border-gray-200">Transport Points</h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Pick Up Point</label>
                                        <input
                                            type="text"
                                            value={formData.pickUpPoint}
                                            onChange={(e) => setFormData({ ...formData, pickUpPoint: e.target.value })}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            placeholder="Enter pickup location"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Drop Off Point</label>
                                        <input
                                            type="text"
                                            value={formData.dropOffPoint}
                                            onChange={(e) => setFormData({ ...formData, dropOffPoint: e.target.value })}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            placeholder="Enter drop-off location"
                                        />
                                    </div>
                                </div>
                            </div>
                            )}

                            {/* Page 3: Location */}
                            {formPage === 3 && (
                            <div className="mb-6">
                                <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-2 border-b border-gray-200">Location *</h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Province *</label>
                                        <select
                                            value={provinceCode}
                                            onChange={(e) => handleProvinceChange(e.target.value)}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            required
                                        >
                                            <option value="">Select province</option>
                                            {provinces.map((p) => (
                                                <option key={p.code} value={p.code}>{p.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">District *</label>
                                        <select
                                            value={districtCode}
                                            onChange={(e) => handleDistrictChange(e.target.value)}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            disabled={!provinceCode}
                                            required
                                        >
                                            <option value="">Select district</option>
                                            {districts.map((d) => (
                                                <option key={d.code} value={d.code}>{d.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Sector *</label>
                                        <select
                                            value={sectorCode}
                                            onChange={(e) => handleSectorChange(e.target.value)}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            disabled={!districtCode}
                                            required
                                        >
                                            <option value="">Select sector</option>
                                            {sectors.map((s) => (
                                                <option key={s.code} value={s.code}>{s.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Cell *</label>
                                        <select
                                            value={cellCode}
                                            onChange={(e) => handleCellChange(e.target.value)}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            disabled={!sectorCode}
                                            required
                                        >
                                            <option value="">Select cell</option>
                                            {cells.map((c) => (
                                                <option key={c.code} value={c.code}>{c.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Village *</label>
                                        <select
                                            value={villageCode}
                                            onChange={(e) => setVillageCode(e.target.value)}
                                            className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                            disabled={!cellCode}
                                            required
                                        >
                                            <option value="">Select village</option>
                                            {villages.map((v) => (
                                                <option key={v.code} value={v.code}>{v.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                </div>
                            </div>
                            )}

                            {/* Navigation Buttons */}
                            <div className="flex gap-3 pt-4 border-t border-gray-200">
                                {formPage > 1 && (
                                    <button
                                        type="button"
                                        onClick={() => setFormPage(formPage - 1)}
                                        className="px-4 py-2 border border-gray-200 rounded-lg hover:bg-gray-50 font-medium"
                                    >
                                        Previous
                                    </button>
                                )}
                                {formPage < 3 ? (
                                    <button
                                        type="button"
                                        onClick={() => setFormPage(formPage + 1)}
                                        className="flex-1 px-4 py-2 bg-emerald-700 text-white rounded-lg hover:bg-emerald-800 font-medium"
                                    >
                                        Next
                                    </button>
                                ) : (
                                    <>
                                        <button
                                            type="button"
                                            onClick={() => {
                                                setShowModal(false);
                                                setFormPage(1);
                                            }}
                                            className="flex-1 px-4 py-2 border border-gray-200 rounded-lg hover:bg-gray-50 font-medium"
                                        >
                                            Cancel
                                        </button>
                                        <button
                                            type="submit"
                                            className="flex-1 px-4 py-2 bg-emerald-700 text-white rounded-lg hover:bg-emerald-800 font-medium"
                                        >
                                            {editingStudent ? 'Update Student' : 'Add Student'}
                                        </button>
                                    </>
                                )}
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Students;
