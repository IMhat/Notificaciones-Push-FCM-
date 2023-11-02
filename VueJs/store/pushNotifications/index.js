import axios from '@/plugins/axios'
import { PushNotifications } from '@capacitor/push-notifications'

export default {
  namespaced: true,
  state: () => ({
    endpoint: 'notification-push',
    pushToken: null,
    notifications: [],
    ...crudState()
}),
  mutations: {
    setPushToken(state, token) {
      state.pushToken = token
    },
    addNotification(state, notification) {
      state.notifications.push(notification)
    },
    clearPushToken(state) {
      state.pushToken = null
    },
    removeNotification(state, index) {
      state.notifications.splice(index, 1)
    }
  },
  actions: {
    // handler iniciador de notificaciones, para mostrarlas en la pantalla NotificationList y registro del token
    async initializePushNotifications({ commit, dispatch }) {
      try {
        // Solicitar permisos de notificación push
        const permissionResult = await PushNotifications.requestPermissions()

        if (permissionResult.receive === 'granted') {
          // Registrar para recibir notificaciones push
          const registration = await PushNotifications.register()
          commit('setPushToken', registration.value.token)

          // Llama a la acción para enviar el token de notificación push al backend
          await dispatch('sendPushTokenToBackend', registration.value.token)

          // Registrar el manejador de notificaciones entrantes
          PushNotifications.addListener('pushNotificationReceived', (notification) => {
            // Aquí procesa la notificación entrante y agrega al estado de Vuex
            commit('addNotification', notification)
          })

          // Obtener notificaciones entregadas (notificaciones permanentes)
          const deliveredNotifications = await PushNotifications.getDeliveredNotifications()

          // Agregar notificaciones entregadas al estado de Vuex
          deliveredNotifications.forEach((notification) => {
            commit('addNotification', notification)
          })
        } else {
          // Permiso denegado
          console.warn('Permisos de notificación push denegados.')
        }
      } catch (error) {
        console.error('Error al registrar el token de notificación push:', error)
      }
    },
    async deinitializePushNotifications({ commit, state, dispatch }) {
      // Elimina el manejador de notificaciones entrantes
      PushNotifications.removeAllListeners('pushNotificationReceived')
      try {
        // Eliminar el token de notificación push
        await PushNotifications.unregister()
        commit('clearPushToken')
        // Elimina el token del backend
        if (state.pushToken) {
          await dispatch('removePushTokenFromBackend', state.pushToken)
        }
      } catch (error) {
        console.error('Error al desregistrar el token de notificación push:', error)
      }
    },
    // elimina notificaciones de la pantalla NotificationList
    async removeDeliveredNotification({ commit }, notificationId) {
      await PushNotifications.removeDeliveredNotifications({ id: notificationId })

      const index = state.notifications.findIndex((notification) => notification.id === notificationId)
      if (index > -1) {
        commit('removeNotification', index)
      }
    },
    // Enviar el token de notificación push al backend
    async sendPushTokenToBackend({ state }, pushToken) {
      try {
        // Realiza una solicitud HTTP al backend para enviar el token
        const response = await axios.post(`${state.endpoint}/add-token`, { pushToken })
        // Maneja la respuesta del backend (puede ser necesario procesar la respuesta)
        return response;
      } catch (error) {
        console.error('Error al enviar el token de notificación push al backend:', error)
        return Promise.reject(error);
      }
    },
    // Eliminar el token de notificación push del backend
    async removePushTokenFromBackend({ state, rootState }, pushToken) {
      try {

        const userId = rootState.auth.user.id
        // Realiza una solicitud HTTP al backend para eliminar el token
        const response = await axios.delete(`${state.endpoint}/del-token/${userId}`)

        console.log(userId)
        // const response = await axios.delete(`${state.endpoint}/del-token/${user.id}`, {
        //   pushToken
        // })
        // Maneja la respuesta del backend (puede ser necesario procesar la respuesta)
        return response
      } catch (error) {
        console.error('Error al eliminar el token de notificación push del backend:', error)
        return Promise.reject(error)
      }
    }
    // Otras acciones relacionadas con notificaciones.
  }
}
