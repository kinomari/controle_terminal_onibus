import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

interface NavItem {
  to: string;
  label: string;
  roles?: Array<'ADMINISTRADOR' | 'SUPERVISOR' | 'OPERADOR' | 'SEGURANCA'>;
}

const NAV: NavItem[] = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/operacoes', label: 'Operacoes' },
  { to: '/incidentes', label: 'Incidentes' },
  { to: '/docas', label: 'Docas' },
  { to: '/estacionamentos', label: 'Estacionamentos' },
  { to: '/vagas', label: 'Vagas' },
  { to: '/veiculos', label: 'Veiculos' },
  { to: '/terminais', label: 'Terminais' },
  { to: '/tipos-incidente', label: 'Tipos de Incidente', roles: ['ADMINISTRADOR'] },
  { to: '/usuarios', label: 'Usuarios', roles: ['ADMINISTRADOR'] },
];

export default function AppLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  const visibleItems = NAV.filter(
    (item) => !item.roles || (user && item.roles.includes(user.perfil))
  );

  return (
    <div className="flex h-full">
      <aside className="w-64 bg-brand text-white flex flex-col">
        <div className="px-6 py-5 border-b border-brand-700">
          <h1 className="text-lg font-bold">City Transporte</h1>
          <p className="text-xs text-brand-100">Gestao de Terminais</p>
        </div>
        <nav className="flex-1 overflow-y-auto py-3">
          {visibleItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `block px-6 py-2 text-sm hover:bg-brand-700 ${
                  isActive ? 'bg-brand-700 font-semibold border-l-4 border-white' : ''
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="p-4 border-t border-brand-700 text-xs">
          <p className="font-semibold">{user?.nome}</p>
          <p className="text-brand-100">{user?.perfil}</p>
          <button
            onClick={handleLogout}
            className="mt-2 text-brand-100 underline hover:text-white"
          >
            Sair
          </button>
        </div>
      </aside>
      <main className="flex-1 overflow-y-auto">
        <div className="p-6 max-w-7xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
