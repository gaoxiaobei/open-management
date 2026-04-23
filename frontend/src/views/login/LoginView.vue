<template>
  <div class="login-page">
    <!-- 左侧信息区 -->
    <div class="login-left">
      <div class="login-left-inner">
        <div class="system-badge">
          <div class="system-badge-logo">政</div>
          <div>
            <div class="system-badge-name">开放管理平台</div>
            <div class="system-badge-en">Open Management System</div>
          </div>
        </div>

        <div class="system-intro">
          <h1 class="system-title">企业内部<br>协同管理平台</h1>
          <p class="system-desc">
            集成审批流程、组织架构、人事档案、资产台账与消息通知，
            为企业内部管理提供统一的数字化工作台。
          </p>
        </div>

        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><Connection /></el-icon>
            </div>
            <div>
              <div class="feature-title">流程审批</div>
              <div class="feature-desc">支持多级审批、转办、催办，全程留痕可追溯</div>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div>
              <div class="feature-title">组织管理</div>
              <div class="feature-desc">部门、岗位、人员权限统一维护，层级清晰</div>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><DocumentChecked /></el-icon>
            </div>
            <div>
              <div class="feature-title">审计日志</div>
              <div class="feature-desc">登录记录与操作日志全量留存，合规可查</div>
            </div>
          </div>
        </div>

        <div class="login-left-footer">
          © {{ currentYear }} 开放管理平台 &nbsp;|&nbsp; 内部系统，请勿外传
        </div>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="login-right">
      <div class="login-card">
        <div class="login-card-header">
          <h2 class="login-card-title">用户登录</h2>
          <p class="login-card-sub">请输入您的账号信息</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              size="large"
            />
          </el-form-item>

          <el-form-item label="验证码" prop="captcha">
            <div class="captcha-row">
              <el-input
                v-model="form.captcha"
                placeholder="请输入验证码"
                size="large"
              />
              <div class="captcha-img-wrap" @click="loadCaptcha" title="点击刷新">
                <img
                  v-if="captchaImage"
                  :src="captchaImage"
                  alt="验证码"
                  class="captcha-img"
                />
                <div v-else class="captcha-placeholder">
                  <span v-if="captchaLoading">加载中...</span>
                  <span v-else>点击获取</span>
                </div>
              </div>
            </div>
          </el-form-item>

          <el-button
            type="primary"
            native-type="submit"
            :loading="loading"
            size="large"
            class="login-submit-btn"
          >
            登 录
          </el-button>
        </el-form>

        <div class="login-tips">
          <el-icon><InfoFilled /></el-icon>
          点击验证码图片可刷新，如遇问题请联系系统管理员
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { Connection, DocumentChecked, InfoFilled, Lock, OfficeBuilding, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCaptcha } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaLoading = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')
const currentYear = computed(() => new Date().getFullYear())

const form = reactive({ username: '', password: '', captcha: '' })
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

async function loadCaptcha() {
  captchaLoading.value = true
  form.captcha = ''
  try {
    const result = await getCaptcha()
    captchaKey.value = result.captchaKey
    captchaImage.value = result.captchaImage
  } catch {
    captchaImage.value = ''
    captchaKey.value = ''
  } finally {
    captchaLoading.value = false
  }
}

onMounted(() => {
  loadCaptcha()
})

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.doLogin({ ...form, captchaKey: captchaKey.value })
    router.push('/')
  } catch {
    loadCaptcha()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  background: #f4f6f8;
}

/* ─── 左侧 ─── */
.login-left {
  flex: 1;
  background: var(--om-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -80px;
  right: -80px;
  width: 320px;
  height: 320px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.login-left::after {
  content: '';
  position: absolute;
  bottom: -120px;
  left: -60px;
  width: 400px;
  height: 400px;
  border: 1px solid rgba(255, 255, 255, 0.04);
  border-radius: 50%;
}

.login-left-inner {
  max-width: 440px;
  width: 100%;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.system-badge {
  display: flex;
  align-items: center;
  gap: 12px;
}

.system-badge-logo {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--om-font-serif);
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  flex-shrink: 0;
}

.system-badge-name {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  letter-spacing: 0.04em;
}

.system-badge-en {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 2px;
  letter-spacing: 0.06em;
}

.system-title {
  font-family: var(--om-font-serif);
  font-size: clamp(32px, 4vw, 48px);
  font-weight: 700;
  color: #ffffff;
  line-height: 1.25;
  margin: 0;
  letter-spacing: 0.04em;
}

.system-desc {
  margin: 16px 0 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.8;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.feature-icon {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 2px;
}

.feature-title {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 4px;
}

.feature-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
  line-height: 1.6;
}

.login-left-footer {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* ─── 右侧 ─── */
.login-right {
  width: 420px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 32px;
  background: #ffffff;
  border-left: 1px solid var(--om-border);
}

.login-card {
  width: 100%;
}

.login-card-header {
  margin-bottom: 28px;
}

.login-card-title {
  font-family: var(--om-font-serif);
  font-size: 26px;
  font-weight: 700;
  color: var(--om-text);
  margin: 0;
  letter-spacing: 0.04em;
}

.login-card-sub {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--om-text-muted);
}

.login-form :deep(.el-form-item__label) {
  font-size: 13px;
  font-weight: 500;
  color: var(--om-text-secondary);
  padding-bottom: 6px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: var(--om-radius-md);
  box-shadow: 0 0 0 1px var(--om-border);
  background: #ffffff;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--om-border-strong);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(26, 58, 92, 0.2);
}

.captcha-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.captcha-row :deep(.el-input) {
  flex: 1;
}

.captcha-img-wrap {
  width: 120px;
  height: 40px;
  flex-shrink: 0;
  border: 1px solid var(--om-border);
  border-radius: var(--om-radius-md);
  overflow: hidden;
  cursor: pointer;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.12s;
}

.captcha-img-wrap:hover {
  border-color: var(--om-primary);
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.captcha-placeholder {
  font-size: 12px;
  color: var(--om-text-muted);
}

.login-submit-btn {
  width: 100%;
  height: 42px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.1em;
  border-radius: var(--om-radius-md);
  background: var(--om-primary);
  border-color: var(--om-primary);
  margin-top: 4px;
}

.login-submit-btn:hover {
  background: var(--om-primary-light);
  border-color: var(--om-primary-light);
}

.login-tips {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-top: 20px;
  padding: 10px 12px;
  background: #f5f7fa;
  border: 1px solid var(--om-border-light);
  border-radius: var(--om-radius-md);
  font-size: 12px;
  color: var(--om-text-muted);
  line-height: 1.6;
}

.login-tips .el-icon {
  margin-top: 2px;
  flex-shrink: 0;
  color: var(--om-info);
}

/* ─── 响应式 ─── */
@media (max-width: 900px) {
  .login-left {
    display: none;
  }

  .login-right {
    width: 100%;
    border-left: none;
    padding: 32px 24px;
  }
}

@media (max-width: 480px) {
  .login-right {
    padding: 24px 16px;
    align-items: flex-start;
    padding-top: 48px;
  }
}
</style>
