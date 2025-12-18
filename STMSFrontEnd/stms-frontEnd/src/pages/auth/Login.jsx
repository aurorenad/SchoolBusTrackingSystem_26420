import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Button from '../../components/Button';
import { Lock, Mail, ArrowRight, Bus } from 'lucide-react';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const { login } = useAuth();
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        await login(email, password);
        setLoading(false);
    };

    return (
        <div className="min-h-screen bg-stone-100 flex items-center justify-center p-4">
            <div className="max-w-5xl w-full bg-amber-50 rounded-2xl shadow-2xl overflow-hidden flex flex-col md:flex-row border border-emerald-900/10">
                {/* Left Side - Hero Section */}
                <div className="md:w-1/2 bg-emerald-950 relative p-12 flex flex-col justify-between hidden md:flex">
                    <div className="absolute inset-0 overflow-hidden">
                        <div className="absolute -top-24 -left-24 w-96 h-96 bg-emerald-800 rounded-full mix-blend-screen filter blur-3xl opacity-20 animate-blob"></div>
                        <div className="absolute -bottom-24 -right-24 w-96 h-96 bg-amber-600 rounded-full mix-blend-screen filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>

                        {/* Abstract Pattern */}
                        <svg className="absolute inset-0 w-full h-full opacity-5" viewBox="0 0 100 100" preserveAspectRatio="none">
                            <defs>
                                <pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse">
                                    <path d="M 10 0 L 0 0 0 10" fill="none" stroke="#fef3c7" strokeWidth="0.5" />
                                </pattern>
                            </defs>
                            <rect width="100%" height="100%" fill="url(#grid)" />
                        </svg>
                    </div>

                    <div className="relative z-10">
                        <div className="flex items-center space-x-3 mb-8">
                            <div className="bg-amber-50/10 p-3 rounded-xl backdrop-blur-sm border border-amber-50/20">
                                <Bus className="h-8 w-8 text-amber-50" />
                            </div>
                            <span className="text-2xl font-bold tracking-tight text-amber-50">STMS</span>
                        </div>
                        <h2 className="text-4xl font-serif font-bold text-amber-50 mb-6 leading-tight">
                            Smart Transport <br />
                            <span className="text-emerald-400">Management</span>
                        </h2>
                        <p className="text-emerald-200 text-lg leading-relaxed max-w-sm">
                            Efficient routes, reliable schedules, and seamless travel for everyone.
                        </p>
                    </div>

                    <div className="relative z-10 flex items-center space-x-2 text-sm text-emerald-300/60">
                        <span>© 2025 STMS Inc.</span>
                    </div>
                </div>

                {/* Right Side - Login Form */}
                <div className="md:w-1/2 p-8 md:p-12 lg:p-16 flex items-center justify-center bg-amber-50">
                    <div className="w-full max-w-md space-y-8">
                        <div className="text-center md:text-left">
                            <h2 className="text-3xl font-bold text-emerald-950 tracking-tight font-serif">Welcome back</h2>
                            <p className="mt-2 text-emerald-800/60">Please enter your details to sign in.</p>
                        </div>

                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="space-y-2">
                                <label className="text-sm font-semibold text-emerald-900 block">Email Address</label>
                                <div className="relative group">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <Mail className="h-5 w-5 text-emerald-700/50 group-focus-within:text-emerald-800 transition-colors" />
                                    </div>
                                    <input
                                        type="email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        className="block w-full pl-11 pr-4 py-3.5 border border-emerald-200/60 rounded-xl focus:ring-2 focus:ring-emerald-800/10 focus:border-emerald-800 transition-all bg-white hover:bg-white focus:bg-white text-emerald-900 placeholder-emerald-900/30"
                                        placeholder="Enter your email"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <div className="flex items-center justify-between">
                                    <label className="text-sm font-semibold text-emerald-900 block">Password</label>
                                    <Link to="/forgot-password" className="text-sm font-medium text-emerald-700 hover:text-emerald-900 transition-colors">
                                        Forgot password?
                                    </Link>
                                </div>
                                <div className="relative group">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <Lock className="h-5 w-5 text-emerald-700/50 group-focus-within:text-emerald-800 transition-colors" />
                                    </div>
                                    <input
                                        type="password"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        className="block w-full pl-11 pr-4 py-3.5 border border-emerald-200/60 rounded-xl focus:ring-2 focus:ring-emerald-800/10 focus:border-emerald-800 transition-all bg-white hover:bg-white focus:bg-white text-emerald-900 placeholder-emerald-900/30"
                                        placeholder="••••••••"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="text-sm text-emerald-800/70">
                                First time?{' '}
                                <Link to="/set-password" className="font-medium text-emerald-700 hover:text-emerald-900 transition-colors">
                                    Set your password
                                </Link>
                            </div>

                            <Button
                                text={
                                    <span className="flex items-center justify-center space-x-2">
                                        <span>{loading ? 'Signing in...' : 'Sign In'}</span>
                                        {!loading && <ArrowRight className="h-5 w-5" />}
                                    </span>
                                }
                                type="submit"
                                variant="brand"
                                className="w-full !py-4 text-base shadow-xl shadow-emerald-900/10 hover:shadow-emerald-900/20 !rounded-xl transition-transform active:scale-[0.98]"
                            />
                        </form>

                        <div className="pt-6 text-center border-t border-emerald-100/50">
                            <p className="text-sm text-emerald-800/60">
                                Don't have an account? <span className="text-emerald-900 font-bold cursor-pointer hover:underline">Contact Admin</span>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
