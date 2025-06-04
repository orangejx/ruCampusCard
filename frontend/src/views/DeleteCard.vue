<template>
  <div class="delete-card">
    <el-card class="card-container">
      <template #header>
        <div class="card-header">
          <h3>校园卡注销</h3>
        </div>
      </template>

      <div class="warning-box">
        <el-alert
          title="注意：校园卡注销后将无法恢复，请谨慎操作！"
          type="warning"
          :closable="false"
          show-icon
        >
          <p>注销前请确保：</p>
          <ol>
            <li>已确认该卡片不再使用</li>
            <li>已处理卡内剩余余额</li>
            <li>已完成所有待处理的交易</li>
          </ol>
        </el-alert>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        label-position="right"
        status-icon
      >
        <el-form-item label="学生ID" prop="studentId">
          <el-input
            v-model="form.studentId"
            placeholder="请输入要注销的学生ID"
            clearable
            @blur="fetchCardInfo"
          ></el-input>
        </el-form-item>

        <template v-if="cardInfo">
          <el-form-item label="学生姓名">
            <el-input v-model="cardInfo.studentName" disabled></el-input>
          </el-form-item>

          <el-form-item label="当前余额">
            <el-input v-model="cardInfo.balance" disabled>
              <template #prepend>¥</template>
            </el-input>
          </el-form-item>

          <el-form-item label="卡片状态">
            <el-tag :type="cardInfo.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ cardInfo.status === 'ACTIVE' ? '正常' : '已注销' }}
            </el-tag>
          </el-form-item>
        </template>

        <el-form-item label="注销原因" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入注销原因"
          ></el-input>
        </el-form-item>

        <el-form-item label="确认注销" prop="confirmation">
          <el-checkbox v-model="form.confirmation">
            我已了解注销后果，确认要注销此校园卡
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-popconfirm
            title="确定要注销这张校园卡吗？"
            confirm-button-text="确定注销"
            cancel-button-text="再想想"
            @confirm="submitForm(formRef)"
          >
            <template #reference>
              <el-button
                type="danger"
                :disabled="!form.confirmation || !cardInfo || cardInfo.status !== 'ACTIVE'"
                :loading="loading"
              >
                <el-icon><Delete /></el-icon>注销校园卡
              </el-button>
            </template>
          </el-popconfirm>
          <el-button @click="resetForm(formRef)">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="注销结果"
      width="30%"
      center
    >
      <div class="result-content">
        <el-result
          :icon="resultInfo.success ? 'success' : 'error'"
          :title="resultInfo.title"
          :sub-title="resultInfo.subTitle"
        >
          <template #extra>
            <el-button type="primary" @click="handleDialogClose">确定</el-button>
          </template>
        </el-result>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Refresh } from '@element-plus/icons-vue'
import axios from 'axios'

const formRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const cardInfo = ref(null)

const resultInfo = reactive({
  success: false,
  title: '',
  subTitle: ''
})

const form = reactive({
  studentId: '',
  reason: '',
  confirmation: false
})

const rules = reactive({
  studentId: [
    { required: true, message: '请输入学生ID', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入注销原因', trigger: 'blur' },
    { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  confirmation: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请确认注销操作'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
})

const fetchCardInfo = async () => {
  if (!form.studentId) return

  try {
    const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/cards/student/${form.studentId}`)
    cardInfo.value = response.data

    if (response.status === 'INACTIVE') {
      ElMessage.warning('该校园卡已经被注销')
    } else if (response.balance > 0) {
      ElMessage.warning('该卡还有余额，建议先处理余额后再注销')
    }
  } catch (error) {
    cardInfo.value = null
    ElMessage.error(error.message || '获取校园卡信息失败')
  }
}

const submitForm = async (formEl) => {
  if (!formEl) return

  await formEl.validate(async (valid) => {
    if (valid) {
      if (!cardInfo.value) {
        ElMessage.warning('请先输入有效的学生ID')
        return
      }

      if (cardInfo.value.status !== 'ACTIVE') {
        ElMessage.warning('该校园卡已经被注销')
        return
      }

      try {
        loading.value = true
        await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/cards/student/${form.studentId}`)

        resultInfo.success = true
        resultInfo.title = '注销成功'
        resultInfo.subTitle = `学生ID为 ${form.studentId} 的校园卡已成功注销`

        dialogVisible.value = true
      } catch (error) {
        resultInfo.success = false
        resultInfo.title = '注销失败'
        resultInfo.subTitle = error.message || '注销过程中发生错误'

        dialogVisible.value = true
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
  cardInfo.value = null
}

const handleDialogClose = () => {
  dialogVisible.value = false
  if (resultInfo.success) {
    resetForm(formRef.value)
  }
}
</script>

<style scoped>
.delete-card {
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

.warning-box {
  margin-bottom: 20px;
}

.warning-box ol {
  margin: 10px 0 0 20px;
  padding: 0;
}

.warning-box li {
  margin-bottom: 5px;
}

.result-content {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
