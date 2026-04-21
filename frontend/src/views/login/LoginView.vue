<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-title">Open Management</div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="验证码" prop="captcha">
          <div class="captcha-row">
            <el-input v-model="form.captcha" placeholder="请输入验证码" style="flex: 1" />
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
        <el-button type="primary" native-type="submit" :loading="loading" style="width: 100%">登录</el-button>
      </el-form>
    </div>
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
.login-container { height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%); }
.login-card { width: 400px; padding: 40px; background: #fff; border-radius: 8px; box-shadow: 0 8px 32px rgba(0,0,0,0.3); }
.login-title { text-align: center; font-size: 24px; font-weight: bold; color: #1a1a2e; margin-bottom: 32px; }
.captcha-row { display: flex; gap: 8px; align-items: center; width: 100%; }
.captcha-img { height: 40px; cursor: pointer; border-radius: 4px; border: 1px solid #dcdfe6; }
.captcha-btn { white-space: nowrap; }
</style>
