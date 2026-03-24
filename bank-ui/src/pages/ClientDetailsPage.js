import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getClientById, getClientCards, createCard, cancelCard } from '../services/api';

function ClientDetailsPage(){
    const {id} = useParams();
    const navigate = useNavigate();

    const [client, setClient] = useState(null);
    const [cards, setCards] = useState([]);
    const [cardType, setCardType] = useState('VISA');
    const [validityYears,setValidityYears] = useState(3);
    const [loading,setLoading] = useState(false);
    const [actionLoading, setActionLoading] = useState(false);
    const [error, setError] = useState(false);
    const [showCardForm, setShowCardForm] = useState(false);

    useEffect(() => {
        loadData();
    }, [id]);

    const loadData = async () => {
        try{
            setLoading(true);
            const [clientRes, cardsRes] = await Promise.all([
                getClientById(id),
                getClientCards(id)
            ])
            setClient(clientRes.data);
            setCards(cardsRes.data);
        }
        catch (err){
            console.error('Ошибка загрузки:', err);
            alert('Не удалось загрузить данные')
        }
        finally {
            setLoading(false);
        }
    }

    const handleCreateCard = async (e) => {
        e.preventDefault();
        setActionLoading(true);
        try{
            await createCard(id,cardType,validityYears);
            await loadData();
            setShowCardForm(false);
            setCardType('VISA');
            setValidityYears(3);
        }
        catch (err){
            console.error('Ошибка создания карты:', err);
            alert('Не удалось создать карту');
        }
        finally {
            setActionLoading(false);
        }
    }

    const handleCancelCard = async (cardId) => {
        setActionLoading(true);
        try{
            await cancelCard(cardId);
            await loadData();
        }
        catch (err){
            console.error('Ошибка аннулирования карты:', err);
            alert('Не удалось аннулировать карту');
        }
        finally {
            setActionLoading(false);
        }
    }

    if (loading) {
        return <div className="text-center mt-5">Загрузка...</div>;
    }

    if (error) {
        return <div className="alert alert-danger">{error}</div>;
    }

    if (!client) {
        return <div className="alert alert-danger">Клиент не найден</div>;
    }

    return (
        <div>
            <button onClick={() => navigate('/clients')} className="btn btn-secondary mb-3">
                Назад к списку
            </button>

            <div className="card mb-4">
                <div className="card-body">
                    <h2 className="card-title">{client.fullName}</h2>
                    <p className="card-text">
                        <strong>Дата рождения:</strong> {client.birthDate} ({client.age} лет)<br />
                        <strong>Email:</strong> {client.email || '—'}<br />
                        <strong>Телефон:</strong> {client.phone || '—'}<br />
                        <strong>Паспорт:</strong> {client.passportNumber || '—'}
                    </p>
                </div>
            </div>

            <div className="d-flex justify-content-between align-items-center mb-3">
                <h3>Карты клиента</h3>
                <button
                    onClick={() => setShowCardForm(!showCardForm)}
                    className="btn btn-primary"
                    disabled={actionLoading}
                >
                    {showCardForm ? 'Отмена' : '+ Выпустить карту'}
                </button>
            </div>

            {showCardForm && (
                <div className="card mb-4">
                    <div className="card-body">
                        <h5>Выпуск новой карты</h5>
                        <form onSubmit={handleCreateCard} className="row g-3">
                            <div className="col-md-5">
                                <label className="form-label">Тип карты</label>
                                <select
                                    className="form-select"
                                    value={cardType}
                                    onChange={(e) => setCardType(e.target.value)}
                                    disabled={actionLoading}
                                >
                                    <option value="VISA">VISA</option>
                                    <option value="Mastercard">Mastercard</option>
                                    <option value="MIR">МИР</option>
                                </select>
                            </div>
                            <div className="col-md-5">
                                <label className="form-label">Срок действия</label>
                                <select
                                    className="form-select"
                                    value={validityYears}
                                    onChange={(e) => setValidityYears(Number(e.target.value))}
                                    disabled={actionLoading}
                                >
                                    <option value="1">1 год</option>
                                    <option value="2">2 года</option>
                                    <option value="3">3 года</option>
                                    <option value="4">4 года</option>
                                    <option value="5">5 лет</option>
                                </select>
                            </div>
                            <div className="col-md-2 d-flex align-items-end">
                                <button
                                    type="submit"
                                    className="btn btn-success w-100"
                                    disabled={actionLoading}
                                >
                                    {actionLoading ? 'Выпуск...' : 'Выпустить'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {cards.length === 0 ? (
                <div className="alert alert-info">
                    У клиента нет карт. Нажмите «Выпустить карту» для создания.
                </div>
            ) : (
                <div className="row">
                    {cards.map(card => (
                        <div key={card.id} className="col-md-6 mb-3">
                            <div className={`card h-100 ${card.active ? 'border-success' : 'border-secondary'}`}>
                                <div className="card-body">
                                    <div className="d-flex justify-content-between align-items-start">
                                        <h5 className="card-title">{card.cardType}</h5>
                                        <span className={`badge ${card.active ? 'bg-success' : 'bg-secondary'}`}>
                                            {card.active ? 'Активна' : 'Аннулирована'}
                                        </span>
                                    </div>
                                    <p className="card-text mt-2">
                                        <strong>Номер карты:</strong> {card.cardNumber}
                                    </p>
                                    <p className="mb-1">
                                        <strong>Истекает:</strong> {card.expiryDate}
                                    </p>
                                    <p className="mb-2">
                                        <strong>Дней до истечения:</strong>{' '}
                                        <span className={card.daysUntilExpired <= 7 ? 'text-danger fw-bold' : ''}>
                                            {card.daysUntilExpired}
                                        </span>
                                    </p>
                                    {card.active && (
                                        <button
                                            onClick={() => handleCancelCard(card.id)}
                                            className="btn btn-danger btn-sm"
                                            disabled={actionLoading}
                                        >
                                            Аннулировать
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default ClientDetailsPage;