<template>
    <el-popover placement="bottom" width="400" trigger="click" popper-class="list-noti"
                v-if="allNotificaciones.length > 0" style="font-size: 1.5rem;">
        <ul class="lista-notificaciones scroll-notification timeline">
            <li class="li-border block" v-for="notificacion in allNotifications" :key="notificacion.id"
                @click="$router.push({ path:  notificacion.tipoobjeto === 'contactos' ? '/contactos/editar/'+notificacion.idactividad : notificacion.tipoobjeto === 'cuentas' ?  '/cuentas/editar/'+notificacion.idactividad :  '/actividades/'+notificacion.idactividad+'/detalles/true' })">
                <div class="avatar">
                    <span class="acronym">{{ getAcronym(notificacion) }}</span>
                </div>
                <div :class="notificacion.estado == 0 ? 'content needtoshow notification-unread' : 'content'">
                    <div class="description">
                        <p>{{ notificacion.idusuariocreaciondesc }}</p>
                        <p>{{ notificacion.title }}</p>
                        <p class="date">{{ notificacion.fechacreacion | moment('DD-MM-YYYY HH:mm') }}</p>
                        <p class="date">{{ notificacion.date | moment('DD-MM-YYYY HH:mm') }}</p>
                        <div v-html="notificacion['asunto']"></div>
                        <div v-html="notificacion.body "></div>
                    </div>
                </div>
                <button class="delete-button" @click="eliminarNotificacion(notificacion.id)">X</button>
            </li>
        </ul>

        <!-- <ul class="lista-notificaciones">
        //TODO: CLEAN TODO//
            <li class="li-border" v-for="notificacion in allNotificaciones" :key="notificacion.id" @click="$router.push({ path: '/fieldservice/editar/'+notificacion.idactividad })">
                <div class="content">
                    <div class="description">
                        <p>{{ notificacion.idusuariocreaciondesc }}{{this.homePage}}</p>
                        <p class="date">{{ notificacion.fechacreacion | moment('DD-MM-YYYY HH:mm') }}</p>
                        <div v-html="notificacion['asunto']"></div>
                    </div>
                </div>
            </li>
        </ul> -->
        <!-- <el-badge :max="99" class="item menuitem" type="danger" slot="reference" :value="filteredNotificaciones.length">
            <i class="fas fa-bell"></i>
        </el-badge> -->
        <el-badge :max="99" class="item menuitem" type="danger" slot="reference" :value="unreadNotificationsCount">
            <i class="fas fa-bell"></i>
        </el-badge>
    </el-popover>
</template>

<style lang="scss" module>
    /* Agregar estilos para la notificación */
    // .li-border{
    //     margin: 0px, 0px, 0px;
    // }
    .notification-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        //padding: 10px;
    }

    /* Estilos para el botón "Eliminar Notificación" */
    .delete-button {
        background-color: #999999;
        color: #000000;
        border: none;
        padding: 5px 10px;
        border-radius: 5px;
        cursor: pointer;
        margin-right: 0px;
    }
    .list-noti {
        top: 57px !important;
        height: 350px;
        overflow-y: auto;
    }

    .notification-unread {
        border: 2px solid red;
        padding: 10px;
        border-radius: 10px;
    }

    .lista-notificaciones {
        padding-left: 0 !important;

        border: 1px solid;
        padding: 10px;
        box-shadow: 5px 10px 8px #888888;

        li {
            border-color: #606f7b !important;
            border-width: 1.5px !important;
            text-decoration: none;
            list-style: none;
            padding: 10px;
            border-bottom: 1px solid #ededed;
            cursor: pointer;
            color: #c3c3c3;
            -webkit-transition: all 0.2s ease-in-out;
            -moz-transition: all 0.2s ease-in-out;
            -ms-transition: all 0.2s ease-in-out;
            -o-transition: all 0.2s ease-in-out;
            transition: all 0.2s ease-in-out;

            &:hover {
                color: #02505d;
                background: #fafafa;
            }
        }
    }

    .content {
        @apply flex flex-row flex-1 p-5 bg-white border border-grey-light rounded-sm relative;
        order: 2;

        &:after, &:before {
            @apply absolute border-solid border-transparent pointer-events-none;
            content: " ";
            right: 100%;
            top: 50%;
            height: 0;
            width: 0;
        }

        &:after {
            border-right-color: config('colors.white');
            border-width: 10px;
            margin-top: -10px;
        }

        &:before {
            border-right-color: config('colors.grey-light');
            border-width: 11px;
            margin-top: -11px;
        }

        .icon {
            @apply flex flex-row items-center justify-center pr-5;
        }

        .description {
            @apply flex-1;

            .date {
                @apply font-light text-xs my-2;
            }
        }
    }

    .li-border {
        border-color: #606f7b !important;
        border-width: 1.5px !important;
    }
</style>

<script>
    import { mapGetters, mapActions } from 'vuex'
    // import get from 'lodash/get'.scroll-notification

    export default {
        data() {
            return {}
        },
        computed: {
            ...mapGetters('fsnotificacion', { allNotificaciones: 'all' }),
            allNotifications() {
                const localNotifications = this.allNotificaciones // Notificaciones locales
                const pushNotifications = this.$store.state.pushNotifications.notifications // Notificaciones push

                // Combina las notificaciones locales y push
                return [...localNotifications, ...pushNotifications]
            },
            filteredNotificaciones: function() {
                return this.allNotificaciones.filter(function(i) {
                    return i.estado === 0
                })
            },
            unreadNotificationsCount() {
            const localNotifications = this.allNotificaciones
            const pushNotifications = this.$store.state.pushNotifications.notifications

            // Filtra las notificaciones locales no leídas
            const unreadLocalNotifications = localNotifications.filter(notification => notification.estado === 0)

            // Calcula la cantidad de notificaciones push no leídas
            const unreadPushNotifications = pushNotifications.filter(notification => !notification.estado)

            // Suma las notificaciones no leídas locales y push
            return unreadLocalNotifications.length + unreadPushNotifications.length
            }
        },
        methods: {
            ...mapActions('pushNotifications', ['removeDeliveredNotification']),
            getAcronym(item) {
                var acatarName = (item['idusuariocreaciondesc'] || '').split(' ').map(word => (word[0] || '').toUpperCase()).join('')
                return acatarName
            },
            channelIcon(channel) {
                switch (channel) {
                    case 'call':
                        return 'fas fa-phone fa-2x text-info'
                    case 'chat':
                        return 'fas fa-comments fa-2x text-info'
                    case 'email':
                        return 'fas fa-envelope fa-2x text-info'
                    case 'sms':
                        return 'fas fa-sms fa-2x text-info'
                    default:
                        return ''
                }
            },
            eliminarNotificacion(notificationId) {
                // Llama a la acción de Vuex para eliminar la notificación entregada
                this.removeDeliveredNotification(notificationId)
            }
        },
        mounted() {
        }
    }
</script>
