import { useState, useEffect } from "react";
import axios from "axios";

const Dashboard = () => {
  const [sheets, setSheets] = useState([]);
  const [userName, setUserName] = useState(localStorage.getItem('userName'));

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold text-blue-600">Hello, {userName}!</h1>

      {/* Sheets Table */}
      <h2 className="text-xl font-semibold mt-6">Active Sheets</h2>
      <table className="w-full border mt-2">
        <thead>
          <tr className="bg-blue-100">
            <th className="p-2">Name</th>
            <th className="p-2">Uploaded By</th>
            <th className="p-2">Size</th>
            <th className="p-2">Permissions</th>
            <th className="p-2">Actions</th>
          </tr>
        </thead>
      </table>
      </div>
  );
};

export default Dashboard;
