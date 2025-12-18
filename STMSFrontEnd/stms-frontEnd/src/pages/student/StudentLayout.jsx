import React from 'react';
import { Outlet } from 'react-router-dom';
import { Calendar, Bell, User } from 'lucide-react';
import Layout from '../../components/Layout';

const StudentLayout = () => {
    const menuItems = [
        { label: 'Schedule', path: '/student/schedule', icon: Calendar },
        { label: 'Profile', path: '/student/profile', icon: User },
        { label: 'Announcements', path: '/student/announcements', icon: Bell },
    ];

    return (
        <Layout menuItems={menuItems}>
            <Outlet />
        </Layout>
    );
};

export default StudentLayout;
