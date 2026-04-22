<template>
  <div class="login-page">
    <section class="login-hero">
      <div class="hero-badge">Open Management / Operations Suite</div>
      <h1 class="hero-title">把人、流程、消息和资产放在同一块操作台上。</h1>
      <p class="hero-copy">
        一个面向企业内部协同的轻量管理前端，集中承接审批、组织、审计和业务数据的日常操作。
      </p>

      <div class="hero-grid">
        <article class="hero-panel">
          <span>Workflow</span>
          <strong>审批待办集中处理</strong>
          <p>从流程发起到节点流转，减少跨模块切换。</p>
        </article>
        <article class="hero-panel">
          <span>Directory</span>
          <strong>组织与权限同屏维护</strong>
          <p>用户、角色、岗位、菜单权限统一管理。</p>
        </article>
        <article class="hero-panel">
          <span>Ledger</span>
          <strong>消息与资产可追踪</strong>
          <p>围绕通知中心和业务台账构建清晰的状态感。</p>
        </article>
      </div>
    </section>

    <section class="login-shell">
      <div class="login-card">
        <div class="login-heading">
          <span class="login-kicker">Secure Entry</span>
          <h2>登录工作台</h2>
          <p>请输入账户、密码与验证码进入系统。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleLogin">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item label="验证码" prop="captcha">
            <div class="captcha-row">
              <el-input v-model="form.captcha" placeholder="请输入验证码" />
              <img
                v-if="captchaImage"
                :src="captchaImage"
                alt="验证码"
                class="captcha-img"
                @click="loadCaptcha"
              />
              <el-button v-else :loading="captchaLoading" @click="loadCaptcha" class="captcha-btn">获取验证码</el-button>
            </div>
          </el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">进入系统</el-button>
        </el-form>

        <div class="login-footer">
          点击验证码可刷新，登录成功后进入统一运营工作台。
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getCaptcha } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaLoading = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')

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
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(380px, 480px);
  gap: 28px;
  padding: clamp(20px, 4vw, 40px);
  background:
    radial-gradient(circle at 14% 16%, rgba(201, 111, 59, 0.22), transparent 26%),
    radial-gradient(circle at 90% 18%, rgba(15, 91, 82, 0.24), transparent 22%),
    linear-gradient(135deg, #f6efe4 0%, #eee3d1 48%, #efe7da 100%);
}

.login-hero,
.login-shell {
  min-height: calc(100vh - clamp(40px, 8vw, 80px));
}

.login-hero {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: clamp(24px, 4vw, 48px);
  border-radius: 32px;
  background:
    linear-gradient(180deg, rgba(255, 253, 249, 0.78), rgba(255, 247, 238, 0.44)),
    radial-gradient(circle at top right, rgba(15, 91, 82, 0.12), transparent 32%);
  border: 1px solid rgba(24, 33, 38, 0.08);
  box-shadow: 0 28px 54px rgba(42, 34, 24, 0.12);
}

.hero-badge,
.login-kicker,
.hero-panel span {
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.hero-badge {
  display: inline-flex;
  align-self: flex-start;
  padding: 10px 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.74);
  color: var(--om-brand);
  font-size: 12px;
  font-weight: 700;
}

.hero-title {
  max-width: 12ch;
  margin: 28px 0 0;
  font-family: var(--om-display-font);
  font-size: clamp(56px, 8vw, 92px);
  line-height: 0.92;
  color: #17312f;
}

.hero-copy {
  max-width: 560px;
  margin: 18px 0 0;
  color: var(--om-ink-soft);
  font-size: 17px;
  line-height: 1.75;
}

.hero-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.hero-panel {
  padding: 22px;
  border-radius: 24px;
  background: rgba(22, 45, 43, 0.9);
  color: rgba(255, 247, 238, 0.92);
  box-shadow: 0 22px 40px rgba(13, 38, 35, 0.18);
}

.hero-panel span {
  display: block;
  margin-bottom: 14px;
  font-size: 11px;
  color: rgba(255, 247, 238, 0.56);
}

.hero-panel strong {
  display: block;
  font-size: 20px;
  line-height: 1.3;
}

.hero-panel p {
  margin: 10px 0 0;
  color: rgba(255, 247, 238, 0.72);
  line-height: 1.65;
}

.login-shell {
  display: grid;
  place-items: center;
}

.login-card {
  width: 100%;
  padding: 30px;
  border-radius: 30px;
  background: rgba(255, 252, 248, 0.92);
  border: 1px solid rgba(24, 33, 38, 0.08);
  box-shadow: 0 30px 60px rgba(42, 34, 24, 0.16);
  backdrop-filter: blur(16px);
}

.login-heading h2 {
  margin: 6px 0 0;
  font-family: var(--om-display-font);
  font-size: 40px;
  line-height: 1;
}

.login-heading p {
  margin: 12px 0 0;
  color: var(--om-ink-soft);
}

.login-card :deep(.el-form) {
  margin-top: 24px;
}

.login-card :deep(.el-form-item__label) {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--om-ink-soft);
}

.login-card :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 16px;
  box-shadow: inset 0 0 0 1px rgba(24, 33, 38, 0.08);
  background: rgba(255, 255, 255, 0.84);
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 132px;
  gap: 10px;
  align-items: center;
  width: 100%;
}

.captcha-img,
.captcha-btn {
  width: 132px;
  height: 48px;
  border-radius: 16px;
}

.captcha-img {
  display: block;
  cursor: pointer;
  border: 1px solid rgba(24, 33, 38, 0.08);
  object-fit: cover;
  background: #fff;
}

.submit-btn {
  width: 100%;
  min-height: 50px;
  margin-top: 6px;
  font-size: 15px;
}

.login-footer {
  margin-top: 18px;
  color: var(--om-ink-soft);
  font-size: 13px;
  line-height: 1.7;
}

@media (max-width: 1200px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .login-hero,
  .login-shell {
    min-height: auto;
  }

  .hero-grid {
    margin-top: 30px;
  }
}

@media (max-width: 820px) {
  .login-page {
    padding: 14px;
  }

  .login-hero,
  .login-card {
    padding: 22px;
    border-radius: 24px;
  }

  .hero-grid {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: clamp(42px, 16vw, 72px);
  }

  .captcha-row {
    grid-template-columns: 1fr;
  }

  .captcha-img,
  .captcha-btn {
    width: 100%;
  }
}
</style>
