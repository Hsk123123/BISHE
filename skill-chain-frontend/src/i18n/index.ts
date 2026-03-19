import { createI18n } from 'vue-i18n'
import zhCN from '@/locales/zh-CN'
import en from '@/locales/en'

const locale = localStorage.getItem('locale') || 'zh-CN'

export default createI18n({
  legacy: false,
  locale,
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    en
  }
})
