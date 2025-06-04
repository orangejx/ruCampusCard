<template>
  <div class="recharge-card">
    <el-card class="card-container">
      <template #header>
        <div class="card-header">
          <h3>校园卡余额充值</h3>
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
          <el-input
            v-model="form.studentId"
            placeholder="请输入学生ID"
            clearable
          ></el-input>
        </el-form-item>

        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="1"
            :precision="2"
            :step="10"
            style="width: 100%"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="支付方式" prop="paymentMethod">
          <el-select v-model="form.paymentMethod" placeholder="请选择支付方式" style="width: 100%">
            <el-option label="支付宝" value="alipay"></el-option>
            <el-option label="微信支付" value="wechat"></el-option>
            <el-option label="银行卡" value="bank"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm(formRef)" :loading="loading">
            <el-icon><Money /></el-icon>充值
          </el-button>
          <el-button @click="resetForm(formRef)">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="充值结果"
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
            <el-button type="primary" @click="dialogVisible = false">确定</el-button>
          </template>
        </el-result>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Money, Refresh } from '@element-plus/icons-vue'
import axios from 'axios'

const formRef = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const resultInfo = reactive({
  success: false,
  title: '',
  subTitle: ''
})

const form = reactive({
  studentId: '',
  amount: 50,
  paymentMethod: 'alipay'
})

const rules = reactive({
  studentId: [
    { required: true, message: '请输入学生ID', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 1, message: '金额必须大于0', trigger: 'blur' }
  ],
  paymentMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
})

const submitForm = async (formEl) => {
  if (!formEl) return

  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        // 调用充值API，传递正数表示充值
        const response = await axios.put(`${import.meta.env.VITE_API_BASE_URL}/cards/student/${form.studentId}/balance`, {
          amount: form.amount
        })

        resultInfo.success = true
        resultInfo.title = '充值成功'
        resultInfo.subTitle = `已成功向学生ID为 ${form.studentId} 的校园卡充值 ¥${form.amount}`

        dialogVisible.value = true
        resetForm(formEl)
      } catch (error) {
        resultInfo.success = false
        resultInfo.title = '充值失败'
        resultInfo.subTitle = error.message || '请检查学生ID是否正确'

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
}
</script>

<style scoped>
.recharge-card {
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

.result-content {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
