import React from 'react';
import { Outlet } from 'react-router-dom';
import { LayoutDashboard, Calendar, Bell, Users } from 'lucide-react';
import Layout from '../../components/Layout';

const DriverLayout = () => {
    const menuItems = [
        { label: 'Dashboard', path: '/driver', icon: LayoutDashboard },
        { label: 'Assignments', path: '/driver/assignments', icon: Users },
        { label: 'My Schedule', path: '/driver/schedule', icon: Calendar },
        { label: 'Announcements', path: '/driver/announcements', icon: Bell },
    ];

    return (
        <Layout menuItems={menuItems}>
            <Outlet />
        </Layout>
    );
};

export default DriverLayout;
