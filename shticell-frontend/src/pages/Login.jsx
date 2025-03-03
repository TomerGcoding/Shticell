import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [userName, setUserName] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleLogin = async () => {
    if (!userName) {
      setErrorMessage("User name is empty. You can't login with an empty user name.");
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8080/WebAppShticell_Web_exploded/login?username=${userName}`,{responseType:"text"});
      
      if (response.status === 200) {
        localStorage.setItem('userName', userName);
        console.log(`The user ${userName} logged in sucessfully`);
        navigate("/dashboard"); 
      } else {
        setErrorMessage(`Something went wrong: ${response.data}`);
      }
    } catch (error) {
      setErrorMessage(`Something went wrong: ${error.message}`);
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      handleLogin();
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <div className="bg-white p-6 shadow-lg rounded-lg w-96">
        <h2 className="text-2xl font-semibold mb-4 text-center">Login</h2>

        {errorMessage && <p className="text-red-500 text-sm">{errorMessage}</p>}

        <input
          type="text"
          className="w-full p-2 border rounded mt-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter your username"
          value={userName}
          onChange={(e) => setUserName(e.target.value)}
          onKeyUp={handleKeyPress}
        />

        <button
          className="w-full bg-blue-500 text-white p-2 rounded mt-4 hover:bg-blue-600"
          onClick={handleLogin}
        >
          Login
        </button>
      </div>
    </div>
  );
};

export default Login;
