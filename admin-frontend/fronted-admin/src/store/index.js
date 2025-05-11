import request from '@/utiles/request'
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    feedbacks: [],
    currentPage: 1,
    totalPages: 1,
    searchQuery: '',
    currentTab: 'all_feedbacks',
    loading: false,
    error: null
  },
  getters: {
    completedFeedbacks: state => {
      return state.feedbacks.filter(feedback => feedback.status === 'Completed')
    },
    uncompletedFeedbacks: state => {
      return state.feedbacks.filter(feedback => feedback.status === 'Uncompleted')
    }
  },
  mutations: {
    SET_FEEDBACKS (state, feedbacks) {
      state.feedbacks = feedbacks
    },
    SET_CURRENT_PAGE (state, page) {
      state.currentPage = page
    },
    SET_TOTAL_PAGES (state, total) {
      state.totalPages = total
    },
    SET_SEARCH_QUERY (state, query) {
      state.searchQuery = query
    },
    SET_CURRENT_TAB (state, tab) {
      state.currentTab = tab
    },
    SET_LOADING (state, loading) {
      state.loading = loading
    },
    SET_ERROR (state, error) {
      state.error = error
    },
    UPDATE_FEEDBACK (state, { id, data }) {
      const index = state.feedbacks.findIndex(f => f.id === id)
      if (index !== -1) {
        state.feedbacks[index] = { ...state.feedbacks[index], ...data }
      }
    }
  },
  actions: {
    async fetchFeedbacks ({ commit }, params = {}) {
      commit('SET_LOADING', true)
      commit('SET_ERROR', null)
      try {
        const response = await request.get('/feedbacks', { params })
        const { data } = response
        commit('SET_FEEDBACKS', data.list)
        commit('SET_TOTAL_PAGES', Math.ceil(data.total / 10))
      } catch (error) {
        commit('SET_ERROR', error.message)
        console.error('Error fetching feedbacks:', error)
      } finally {
        commit('SET_LOADING', false)
      }
    },
    async updateFeedback ({ commit, dispatch }, { id, data }) {
      commit('SET_LOADING', true)
      commit('SET_ERROR', null)
      try {
        await request.put(`/feedbacks/${id}`, data)

        commit('UPDATE_FEEDBACK', { id, data })

        await dispatch('fetchFeedbacks')
        return true
      } catch (error) {
        commit('SET_ERROR', error.message)
        console.error('Error updating feedback:', error)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    async deleteFeedback ({ dispatch }, id) {
      try {
        await request.delete(`/feedbacks/${id}`)
        await dispatch('fetchFeedbacks', { page: 1 })
        return true
      } catch (error) {
        console.error('Error deleting feedback:', error)
        throw error
      }
    }
  },
  modules: {
  }
})
