import { useAuth } from "../../contexts/AuthContext";

const { login } = useAuth();

const handleSubmit = async (e) => {
    e.preventDefault();

    try {
        await login(email, password);
        console.log("Login successful");
    } catch (err) {
        console.error(err);
    }
};