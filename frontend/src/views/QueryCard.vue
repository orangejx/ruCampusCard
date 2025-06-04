<template>
  <div class="query-card">
    <el-card class="search-container">
      <template #header>
        <div class="card-header">
          <h3>校园卡信息查询</h3>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学生ID">
          <el-input
            v-model="searchForm.studentId"
            placeholder="请输入学生ID"
            clearable
            @keyup.enter="handleSearch"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="cardInfo" class="result-container">
      <template #header>
        <div class="card-header">
          <h3>查询结果</h3>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生ID">{{ cardInfo.studentId }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ cardInfo.studentName }}</el-descriptions-item>
        <el-descriptions-item label="当前余额">
          <span class="balance">¥ {{ cardInfo.balance }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="最后使用时间">
          {{ formatDate(cardInfo.updatedAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="卡片Id">{{ cardInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="卡片状态">
          <el-tag :type="cardInfo.status === 'ACTIVE' ? 'success' : 'danger'">
            {{ cardInfo.status === 'ACTIVE' ? '正常' : '已注销' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-if="cardInfo" class="transactions-container">
      <template #header>
        <div class="card-header">
          <h3>最近交易记录</h3>
        </div>
      </template>
      <el-table :data="transactions" style="width: 100%" v-loading="transactionsLoading">
        <el-table-column prop="time" label="交易时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.time) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="交易类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'RECHARGE' ? 'success' : 'warning'">
              {{ scope.row.type === 'RECHARGE' ? '充值' : '消费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="交易金额" width="120">
          <template #default="scope">
            <span :class="scope.row.type === 'RECHARGE' ? 'income' : 'expense'">
              {{ scope.row.type === 'RECHARGE' ? '+' : '-' }} ¥{{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="交易描述"></el-table-column>
      </el-table>
    </el-card>

    <el-empty v-if="showEmpty" description="未找到相关校园卡信息"></el-empty>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import axios from 'axios'

const searchForm = reactive({
  studentId: ''
})

const loading = ref(false)
const transactionsLoading = ref(false)
const cardInfo = ref(null)
const transactions = ref([])
const showEmpty = ref(false)

const handleSearch = async () => {
  if (!searchForm.studentId) {
    ElMessage.warning('请输入学生ID')
    return
  }

  try {
    loading.value = true
    showEmpty.value = false
    const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/cards/student/${searchForm.studentId}`)
    cardInfo.value = response.data
    // 这里假设API返回的数据中包含了交易记录
    // 实际项目中可能需要单独调用获取交易记录的API
    transactions.value = response.data.transactions || []
  } catch (error) {
    cardInfo.value = null
    transactions.value = []
    showEmpty.value = true
    ElMessage.error(error.message || '查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.studentId = ''
  cardInfo.value = null
  transactions.value = []
  showEmpty.value = false
}

const formatDate = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
</script>

<style scoped>
.query-card {
  max-width: 1000px;
  margin: 0 auto;
}

.search-container,
.result-container,
.transactions-container {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.search-form {
  display: flex;
  justify-content: center;
  align-items: center;
}

.balance {
  font-size: 18px;
  font-weight: bold;
  color: #67c23a;
}

.income {
  color: #67c23a;
}

.expense {
  color: #f56c6c;
}

:deep(.el-descriptions) {
  margin-bottom: 20px;
}

:deep(.el-descriptions__label) {
  width: 120px;
}
</style>
