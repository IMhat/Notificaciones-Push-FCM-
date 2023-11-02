import Vue from 'vue'
import Vuex from 'vuex'

import auth from './auth'

import dashboard from './dashboard'
import casos from './casos'
import usuarios from './usuarios'
import contactos from './contactos'
import procesos from './procesos'
import pasos from './pasos'
import multityping from './multityping'
import reportedash from './reportedash'

import cuentas from './cuentas'
import grupos from './grupos'
import roles from './roles'
import acuerdos from './acuerdos'
import prioridades from './prioridades'
import categorias from './categorias'
import subcategorias from './subcategorias'
import productos from './productos'
import estados from './estados'
import tipos from './tipos'
import subtipos from './subtipos'
import empresas from './empresas'
import permisos from './permisos'
import navegacion from './navegacion'
import logauditoria from './logauditoria'
import notas from './notas'
import horarios from './horarios'
import dias from './dias'
import archivos from './archivos'
import extraccion from './extraccion'
import correos from './correos'
import fieldservice from './fieldservice'
import encuestas from './encuestas'
import customfields from './customfields'
import reportes from './reportes'
import proyectos from './proyectos'
import informes from './informes'
import parametros from './parametros'
import reporteinc from './reporteinc'
import conceptos from './conceptos'
import estadisticas from './estadisticas'
import licencia from './licencia'
import sistemasexternos from './sistemasexternos'
import company from './company'
import complice from './complice'
import chequeo from './chequeo'
import misreportes from './misreportes'
import feriados from './feriados'
import misproyectos from './misproyectos'
import track from './fieldservice/track'
import emailcontent from './emailcontent'
import faqs from './faqs'
import notificaciones from './notificaciones'
import redashboard from './redashboard'
import gruposkill from './gruposkill'
import direcciones from './direcciones'
import fsnotificacion from './fieldservice/notificacion'
import dynforms from './dynforms'
import bookings from './bookings'
import book from './cp/book'
import authcp from './cp/auth'
import tplemail from './tplemail'
import tplrespemail from './tplrespemail'
import oportunidades from './oportunidades'
import faseoportunidades from './faseoportunidades'
import motivoCierreOportunidades from './motivoCierreOportunidades'
import notasgroup from './notasgroup'
import pushNotifications from './pushNotifications'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        open: false,
        loading: 0
    },
    getters: {
        open: state => state.open,
        loading: state => state.loading > 0
    },
    mutations: {
        setSidebarState(state) {
            state.open = !state.open
        },
        setLoading(state, value) {
            state.loading += value
        }
    },
    actions: {
        toggleSidebar({ commit }) {
            commit('setSidebarState')
        }
    },
    modules: {
        auth,

        dashboard,
        casos,
        usuarios,
        contactos,
        procesos,
        pasos,
        cuentas,
        grupos,
        roles,
        acuerdos,
        prioridades,
        categorias,
        subcategorias,
        productos,
        estados,
        tipos,
        subtipos,
        empresas,
        permisos,
        navegacion,
        logauditoria,
        notas,
        horarios,
        dias,
        archivos,
        extraccion,
        correos,
        sistemasexternos,
        fieldservice,
        encuestas,
        customfields,
        reportes,
        proyectos,
        informes,
        parametros,
        reporteinc,
        conceptos,
        estadisticas,
        company,
        licencia,
        complice,
        chequeo,
        multityping,
        misreportes,
        reportedash,
        feriados,
        misproyectos,
        track,
        emailcontent,
        faqs,
        notificaciones,
        redashboard,
        gruposkill,
        direcciones,
        fsnotificacion,
        dynforms,
        bookings,
        authcp,
        book,
        tplemail,
        tplrespemail,
        oportunidades,
        faseoportunidades,
        motivoCierreOportunidades,
        notasgroup,

        pushNotifications
    }
})
