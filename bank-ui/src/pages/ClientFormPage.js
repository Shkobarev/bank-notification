import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { createClient } from '../services/api';
import 'react-phone-number-input/style.css';
import PhoneInput from 'react-phone-number-input';
import { PatternFormat } from 'react-number-format';

function ClientFormPage() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [formData, setFormData] = useState({
        fullName: '',
        birthDate: '',
        email: '',
        phone: '',
        passportNumber: ''
    });

    const handleChange = (e) => {
        const {name,value} = e.target;
        setFormData(prev => ({...prev,[name]: value}));
    }

    const handlePhoneChange = (value) => {
        setFormData(prev => ({ ...prev, phone: value || '' }));
    };

    const handlePassportNumberChange = (values) => {
        const { value } = values;
        setFormData(prev => ({ ...prev, passportNumber: value || '' }));
    };


    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try{
            const dataToSend = {
                fullName: formData.fullName,
                birthDate: formData.birthDate,
                email: formData.email && formData.email.trim() !== '' ? formData.email : null,
                phone: formData.phone && formData.phone.trim() !== '' ? formData.phone : null,
                passportNumber: formData.passportNumber && formData.passportNumber.trim() !== '' ? formData.passportNumber : null
            };
            await createClient(dataToSend);
            navigate('/clients');
        }
        catch (err){
            console.error('Ошибка создания:',err);

            console.error('Ответ сервера:', err.response);
            console.error('Данные ошибки:', err.response?.data);

            const message = err.response?.data?.message || err.response?.data?.error || 'Ошибка создания клиента';
            setError(message);
        }
        finally {
            setLoading(false);
        }
    }

    return (
        <div className="row justify-content-center">
            <div className={"col-md-6"}>
                <h2 className="mb-4">Новый клиент</h2>

                {error && (<div className="alert alert-danger">{error}</div>)}

                <form onSubmit={handleSubmit}>
                    <div className={"md-3"}>
                        <label className="form-label">ФИО *</label>
                        <input
                            type="text"
                            name="fullName"
                            className="form-control"
                            value={formData.fullName}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Дата рождения *</label>
                        <input
                            type="date"
                            name="birthDate"
                            className="form-control"
                            value={formData.birthDate}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Email</label>
                        <input
                            type="email"
                            name="email"
                            className="form-control"
                            value={formData.email}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Телефон</label>
                        <PhoneInput
                            international
                            defaultCountry="RU"
                            value={formData.phone}
                            onChange={handlePhoneChange}
                            className="form-control"
                            placeholder="+7 999 999 99 99"
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Паспорт</label>
                        <PatternFormat
                            format="#### ######"
                            mask="_"
                            name="passportNumber"
                            className="form-control"
                            value={formData.passportNumber}
                            onValueChange={handlePassportNumberChange}
                            placeholder="1234 567890"
                        />
                    </div>

                    <div className="d-flex gap-2">
                        <button
                            type="submit"
                            className="btn btn-primary"
                            disabled={loading}
                        >
                            {loading ? 'Сохранение...' : 'Сохранить'}
                        </button>
                        <Link to="/clients" className="btn btn-secondary">
                            Отмена
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default ClientFormPage;