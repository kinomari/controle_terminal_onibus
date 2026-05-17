import { Routes, Route, Navigate } from 'react-router-dom';
import PrivateRoute from './PrivateRoute';
import AppLayout from '../layouts/AppLayout';
import Login from '../pages/Login';
import Dashboard from '../pages/Dashboard';
import Terminais from '../pages/Terminais';
import Docas from '../pages/Docas';
import Estacionamentos from '../pages/Estacionamentos';
import Vagas from '../pages/Vagas';
import Veiculos from '../pages/Veiculos';
import Operacoes from '../pages/Operacoes';
import OperacaoDetalhe from '../pages/OperacaoDetalhe';
import TiposIncidente from '../pages/TiposIncidente';
import Incidentes from '../pages/Incidentes';
import Usuarios from '../pages/Usuarios';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route
        path="/"
        element={
          <PrivateRoute>
            <AppLayout />
          </PrivateRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="terminais" element={<Terminais />} />
        <Route path="docas" element={<Docas />} />
        <Route path="estacionamentos" element={<Estacionamentos />} />
        <Route path="vagas" element={<Vagas />} />
        <Route path="veiculos" element={<Veiculos />} />
        <Route path="operacoes" element={<Operacoes />} />
        <Route path="operacoes/:id" element={<OperacaoDetalhe />} />
        <Route path="tipos-incidente" element={
          <PrivateRoute roles={['ADMINISTRADOR']}><TiposIncidente /></PrivateRoute>
        } />
        <Route path="incidentes" element={<Incidentes />} />
        <Route path="usuarios" element={
          <PrivateRoute roles={['ADMINISTRADOR']}><Usuarios /></PrivateRoute>
        } />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
