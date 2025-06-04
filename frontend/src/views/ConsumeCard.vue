<template>
  <div class="consume-card">
    <el-card class="card-container">
      <template #header>
        <div class="card-header">
          <h3>校园卡消费</h3>
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
        </template>

        <el-form-item label="消费类型" prop="consumeType">
          <el-select v-model="form.consumeType" placeholder="请选择消费类型" style="width: 100%">
            <el-option label="食堂就餐" value="CANTEEN"></el-option>
            <el-option label="超市购物" value="SUPERMARKET"></el-option>
            <el-option label="图书文具" value="BOOKSTORE"></el-option>
            <el-option label="其他" value="OTHER"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="消费金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0.01"
            :max="cardInfo ? cardInfo.balance : 9999"
            :precision="2"
            :step="1"
            style="width: 100%"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="消费说明" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="2"
            placeholder="请输入消费说明（选填）"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm(formRef)" :loading="loading">
            <el-icon><ShoppingCart /></el-icon>确认消费
          </el-button>
          <el-button @click="resetForm(formRef)">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="消费结果"
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
import { ShoppingCart, Refresh } from '@element-plus/icons-vue'
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
  consumeType: '',
  amount: 0,
  description: ''
})

const rules = reactive({
  studentId: [
    { required: true, message: '请输入学生ID', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  consumeType: [
    { required: true, message: '请选择消费类型', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入消费金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '金额必须大于0', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ]
})

const fetchCardInfo = async () => {
  if (!form.studentId) return

  try {
    const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/cards/student/${form.studentId}`)
    if (response.data.code === 200) {
      if (response.data.data) {
        cardInfo.value = response.data.data
        if (cardInfo.value.status === 'INACTIVE') {
          ElMessage.warning('该校园卡已注销，无法进行消费')
          cardInfo.value = null
        }
      } else {
        cardInfo.value = null
        ElMessage.warning('未找到校园卡信息')
      }
    } else {
      cardInfo.value = null
      ElMessage.error(response.data.msg || '获取校园卡信息失败')
    }
  } catch (error) {
    cardInfo.value = null
    ElMessage.error(error.response?.data?.msg || error.message || '获取校园卡信息失败')
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

      if (form.amount > cardInfo.value.balance) {
        ElMessage.warning('余额不足')
        return
      }

      try {
        loading.value = true
        // 调用消费API，传递负数表示消费
        const response = await axios.put(`${import.meta.env.VITE_API_BASE_URL}/cards/student/${form.studentId}/balance`, {
          amount: -form.amount,
          // type: form.consumeType,
          // description: form.description
        })

        if (response.data.code === 200) {
          resultInfo.success = true
          resultInfo.title = '消费成功'
          resultInfo.subTitle = `已成功从学生ID为 ${form.studentId} 的校园卡扣除 ¥${form.amount}，${response.data.msg}`

          dialogVisible.value = true
          resetForm(formEl)
          cardInfo.value = null
        } else {
          resultInfo.success = false
          resultInfo.title = '消费失败'
          resultInfo.subTitle = response.data.msg || '请检查卡片状态或余额是否充足'
          dialogVisible.value = true
        }
      } catch (error) {
        resultInfo.success = false
        resultInfo.title = '消费失败'
        resultInfo.subTitle = error.response?.data?.msg || error.message || '请检查卡片状态或余额是否充足'
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
</script>

<style scoped>
.consume-card {
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
