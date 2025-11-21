import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*', // Matches requests to /api/anything
        destination: 'http://localhost:1299/:path*', // Rewrites them to your backend server
      },
    ];
  },
};

export default nextConfig;
