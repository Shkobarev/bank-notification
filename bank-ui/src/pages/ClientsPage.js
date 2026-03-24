import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllClients, deleteClient } from '../services/api';

function ClientsPage(){
    const [clients, setClients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadClients().catch(err => {console.error('Ошибка загрузки:', err)})
    }, []);

    const loadClients = async () => {
        try{
            setLoading(true);
            const response = await getAllClients();
            setClients(response.data);
            setError(null);
        }
        catch (err){
            console.error('Ошибка загрузки', err);
            setError('Не удалось загрузить список клиентов');
        }
        finally {
            setLoading(false);
        }
    }

    const handleDelete = async (id,name) => {
            //if(!window.confirm(`Вы уверены, что хотите удалить клиента "${name}"?`)) return;

            try{
                await deleteClient(id);
                await loadClients();
            }
            catch (err){
                console.error('Ошибка:', err);
                alert('Не удалось удалить клиента. Возможно, у него есть активные карты.');
            }
    }

    if (loading) {
        return <div className="text-center mt-5">Загрузка...</div>;
    }

    if (error) {
        return <div className="alert alert-danger">{error}</div>;
    }

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Клиенты</h2>
                <Link to="/clients/new" className="btn btn-primary">
                    Создать нового клиента
                </Link>
            </div>

            {clients.length === 0 ? (
                <div className="alert alert-info">Нет клиентов. Создайте первого!</div>
            ) : (
                <div className="table-responsive">
                    <table className="table table-striped table-hover">
                        <thead>
                        <tr>
                            <th>ФИО</th>
                            <th>Дата рождения</th>
                            <th>Возраст</th>
                            <th>Email</th>
                            <th>Телефон</th>
                            <th>Карт</th>
                            <th>Действие</th>
                        </tr>
                        </thead>
                        <tbody>
                        {clients.map(client => (
                            <tr key={client.id}>
                                <td>
                                    <Link to={`/clients/${client.id}`} className="text-decoration-none">
                                        {client.fullName}
                                    </Link>
                                </td>
                                <td>{client.birthDate}</td>
                                <td>{client.age}</td>
                                <td>{client.email || '—'}</td>
                                <td>{client.phone || '—'}</td>
                                <td>
                                        <span>
                                            {client.cardIds?.length || 0}
                                        </span>
                                </td>
                                <td>
                                    <button
                                        onClick={() => handleDelete(client.id, client.fullName)}
                                        className="btn btn-sm btn-outline-danger"
                                    >
                                        Удалить
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

export default ClientsPage;