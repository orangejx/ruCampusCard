<template>
  <div class="create-card">
    <el-card class="card-container">
      <template #header>
        <div class="card-header">
          <h3>创建校园卡</h3>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        label-position="right"
        status-icon
      >
        <el-form-item label="学生ID" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生ID"></el-input>
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入学生姓名"></el-input>
        </el-form-item>
        <el-form-item label="初始余额" prop="balance">
          <el-input-number
            v-model="form.balance"
            :min="0"
            :precision="2"
            :step="10"
            style="width: 100%"
          ></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm(formRef)" :loading="loading">创建</el-button>
          <el-button @click="resetForm(formRef)">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  studentId: '',
  studentName: '',
  balance: 0
})

const rules = reactive({
  studentId: [
    { required: true, message: '请输入学生ID', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  studentName: [
    { required: true, message: '请输入学生姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  balance: [
    { required: true, message: '请输入初始余额', trigger: 'blur' }
  ]
})

const submitForm = async (formEl) => {
  if (!formEl) return

  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const response = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/cards`, {
          studentId: form.studentId,
          studentName: form.studentName,
          balance: form.balance
        })

        if (response.data.code === 200) {
          ElMessage({
            message: response.data.msg || '校园卡创建成功',
            type: 'success'
          })
          resetForm(formEl)
        } else {
          ElMessage({
            message: response.data.msg || '校园卡创建失败',
            type: 'error'
          })
        }
      } catch (error) {
        ElMessage({
          message: error.response?.data?.msg || error.message || '校园卡创建失败',
          type: 'error'
        })
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
}
</script>

<style scoped>
.create-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-container {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}
</style>