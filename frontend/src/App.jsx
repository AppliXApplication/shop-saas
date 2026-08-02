import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import ResiduePage from './pages/ResiduePage'
import GoodsCardPage from './pages/GoodsCardPage'
import './styles/global.css'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<ResiduePage />} />
        <Route path="/goods/:id" element={<GoodsCardPage />} />
      </Routes>
    </BrowserRouter>
  )
}


