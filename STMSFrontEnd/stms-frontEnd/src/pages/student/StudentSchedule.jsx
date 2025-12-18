import React, { useState, useEffect } from 'react';
import { Calendar, MapPin, Clock, Bus, ArrowRightLeft } from 'lucide-react';
import { scheduleService } from '../../services/scheduleService';
import { busService } from '../../services/busService';
import { studentService } from '../../services/studentService';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const StudentSchedule = () => {
    const { user } = useAuth();
    const [schedules, setSchedules] = useState([]);
    const [buses, setBuses] = useState([]);
    const [busStudentCounts, setBusStudentCounts] = useState({});
    const [student, setStudent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [showChangeBusModal, setShowChangeBusModal] = useState(false);
    const [selectedBusId, setSelectedBusId] = useState('');
    const [selectedScheduleId, setSelectedScheduleId] = useState(null);

    useEffect(() => {
        if (user?.email) {
            fetchData();
        }
    }, [user]);

    const fetchData = async () => {
        setLoading(true);
        try {
            // Fetch all schedules
            const schedulesData = await scheduleService.getAll();
            setSchedules(schedulesData || []);

            // Fetch all buses
            const busesData = await busService.getAll();
            setBuses(busesData || []);

            // Fetch current student info
            try {
                const studentsData = await studentService.getAll();
                const currentStudent = studentsData.find(s => s.email?.toLowerCase() === user.email?.toLowerCase());
                setStudent(currentStudent);

                // Calculate student count for each bus
                const counts = {};
                for (const bus of busesData || []) {
                    try {
                        const studentsOnBus = await studentService.getByBus(bus.id);
                        counts[bus.id] = studentsOnBus.length;
                    } catch (error) {
                        counts[bus.id] = 0;
                    }
                }
                setBusStudentCounts(counts);
            } catch (error) {
                console.error('Error fetching student:', error);
            }
        } catch (error) {
            console.error('Error fetching data:', error);
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

    const getBusCapacity = (busId) => {
        const bus = buses.find(b => b.id === busId);
        if (!bus) return { current: 0, total: 0 };
        
        const currentCount = busStudentCounts[busId] || 0;
        return { current: currentCount, total: bus.capacity };
    };

    const handleChangeBus = (scheduleId) => {
        setSelectedScheduleId(scheduleId);
        setShowChangeBusModal(true);
        setSelectedBusId('');
    };

    const handleConfirmBusChange = async () => {
        if (!student || !selectedBusId) {
            toast.error('Please select a bus');
            return;
        }

        try {
            // Check if bus has capacity
            const bus = buses.find(b => b.id === Number(selectedBusId));
            if (!bus) {
                toast.error('Bus not found');
                return;
            }

            // Get current students on bus (excluding current student if they're already on it)
            const studentsOnBus = await studentService.getByBus(Number(selectedBusId));
            const currentCount = student?.bus?.id === Number(selectedBusId) 
                ? studentsOnBus.length - 1 
                : studentsOnBus.length;
            
            if (currentCount >= bus.capacity) {
                toast.error('This bus is at full capacity. Please select another bus.');
                return;
            }

            // Assign bus to student
            await studentService.assignBus(student.id, Number(selectedBusId));
            toast.success('Bus changed successfully');
            setShowChangeBusModal(false);
            setSelectedBusId('');
            setSelectedScheduleId(null);
            
            // Refresh data
            await fetchData();
            
            // Refresh bus student counts
            const counts = {};
            for (const bus of buses) {
                try {
                    const studentsOnBus = await studentService.getByBus(bus.id);
                    counts[bus.id] = studentsOnBus.length;
                } catch (error) {
                    counts[bus.id] = 0;
                }
            }
            setBusStudentCounts(counts);
        } catch (error) {
            console.error('Error changing bus:', error);
            const errorMsg = error?.response?.data || error?.message || 'Failed to change bus';
            toast.error(typeof errorMsg === 'string' ? errorMsg : 'Failed to change bus');
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
                <h1 className="text-3xl font-bold text-gray-900">Bus Schedules</h1>
                <p className="text-gray-600 mt-1">View all bus schedules and change your assigned bus</p>
            </div>

            {loading ? (
                <div className="bg-white rounded-xl shadow-md p-8 text-center">
                    <div className="text-gray-500">Loading schedules...</div>
                </div>
            ) : schedules.length > 0 ? (
                <div className="space-y-6">
                    {daysOfWeek.map((day) => {
                        const daySchedules = schedules.filter(s => s.dayOfWeek?.toUpperCase() === day);
                        if (daySchedules.length === 0) return null;

                        return (
                            <div key={day} className="bg-white rounded-xl shadow-md overflow-hidden">
                                <div className="bg-emerald-700 p-4 text-white">
                                    <h3 className="text-lg font-bold">{dayLabels[day] || day}</h3>
                                </div>
                                <div className="p-6">
                                    <div className="space-y-4">
                                        {daySchedules.map((schedule) => {
                                            const isAssigned = student?.bus?.id === schedule.bus?.id;
                                            const busCapacity = getBusCapacity(schedule.bus?.id);
                                            const isFull = busCapacity.current >= busCapacity.total;

                                            return (
                                                <div
                                                    key={schedule.id}
                                                    className={`border rounded-lg p-4 ${
                                                        isAssigned
                                                            ? 'border-emerald-500 bg-emerald-50'
                                                            : 'border-gray-200 hover:bg-gray-50'
                                                    }`}
                                                >
                                                    <div className="flex items-start justify-between">
                                                        <div className="flex-1 space-y-3">
                                                            {schedule.route && (
                                                                <div className="flex items-center gap-2">
                                                                    <MapPin className="h-5 w-5 text-emerald-700" />
                                                                    <span className="font-semibold text-gray-900">
                                                                        {schedule.route.name || 'N/A'}
                                                                    </span>
                                                                </div>
                                                            )}
                                                            <div className="grid grid-cols-2 gap-4">
                                                                <div className="flex items-center gap-2">
                                                                    <Clock className="h-4 w-4 text-gray-400" />
                                                                    <div>
                                                                        <p className="text-xs text-gray-500">Departure</p>
                                                                        <p className="text-sm font-medium text-gray-900">
                                                                            {formatTime(schedule.departureTime)}
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                                <div className="flex items-center gap-2">
                                                                    <Clock className="h-4 w-4 text-gray-400" />
                                                                    <div>
                                                                        <p className="text-xs text-gray-500">Arrival</p>
                                                                        <p className="text-sm font-medium text-gray-900">
                                                                            {formatTime(schedule.arrivalTime)}
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            {schedule.bus && (
                                                                <div className="flex items-center gap-2">
                                                                    <Bus className="h-4 w-4 text-gray-400" />
                                                                    <div>
                                                                        <p className="text-xs text-gray-500">Bus</p>
                                                                        <p className="text-sm font-medium text-gray-900">
                                                                            {schedule.bus.plateNumber || 'N/A'}
                                                                        </p>
                                                                        <p className="text-xs text-gray-500">
                                                                            Capacity: {busCapacity.current}/{busCapacity.total}
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                            )}
                                                            {isAssigned && (
                                                                <span className="inline-block px-3 py-1 bg-emerald-100 text-emerald-700 rounded-full text-xs font-medium">
                                                                    Your Assigned Bus
                                                                </span>
                                                            )}
                                                        </div>
                                                        {schedule.bus && !isAssigned && (
                                                            <button
                                                                onClick={() => handleChangeBus(schedule.id)}
                                                                disabled={isFull}
                                                                className={`ml-4 px-4 py-2 rounded-lg text-sm font-medium transition ${
                                                                    isFull
                                                                        ? 'bg-gray-200 text-gray-500 cursor-not-allowed'
                                                                        : 'bg-emerald-100 text-emerald-700 hover:bg-emerald-200'
                                                                }`}
                                                            >
                                                                <ArrowRightLeft className="h-4 w-4 inline mr-1" />
                                                                {isFull ? 'Full' : 'Change Bus'}
                                                            </button>
                                                        )}
                                                    </div>
                                                </div>
                                            );
                                        })}
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            ) : (
                <div className="bg-white rounded-xl shadow-md p-12 text-center">
                    <Calendar className="h-16 w-16 mx-auto mb-4 text-gray-400" />
                    <p className="text-xl font-medium text-gray-900">No schedules available</p>
                    <p className="text-sm text-gray-500 mt-2">Schedules will appear here once created by an administrator</p>
                </div>
            )}

            {/* Change Bus Modal */}
            {showChangeBusModal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-xl p-6 w-full max-w-md">
                        <h2 className="text-xl font-bold mb-4">Change Bus</h2>
                        <p className="text-sm text-gray-600 mb-4">
                            Select a new bus for this schedule. Only buses with available capacity are shown.
                        </p>
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Select Bus</label>
                                <select
                                    value={selectedBusId}
                                    onChange={(e) => setSelectedBusId(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                                    required
                                >
                                    <option value="">Select bus</option>
                                    {buses
                                        .filter((bus) => {
                                            const capacity = getBusCapacity(bus.id);
                                            // Exclude current bus and only show buses with space
                                            return bus.id !== student?.bus?.id && capacity.current < capacity.total;
                                        })
                                        .map((bus) => {
                                            const capacity = getBusCapacity(bus.id);
                                            return (
                                                <option key={bus.id} value={bus.id}>
                                                    {bus.plateNumber} (Available: {capacity.total - capacity.current}/{capacity.total})
                                                </option>
                                            );
                                        })}
                                </select>
                            </div>
                            <div className="flex gap-3 pt-2">
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowChangeBusModal(false);
                                        setSelectedBusId('');
                                        setSelectedScheduleId(null);
                                    }}
                                    className="flex-1 px-4 py-2 border border-gray-200 rounded-lg hover:bg-gray-50 font-medium"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="button"
                                    onClick={handleConfirmBusChange}
                                    className="flex-1 px-4 py-2 bg-emerald-700 text-white rounded-lg hover:bg-emerald-800 font-medium"
                                >
                                    Change Bus
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default StudentSchedule;
