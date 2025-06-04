<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeIndex"
          class="el-menu-vertical"
          router
          @select="handleSelect"
        >
          <el-menu-item index="/create">
            <el-icon><Plus /></el-icon>
            <span>创建校园卡</span>
          </el-menu-item>
          <el-menu-item index="/query">
            <el-icon><Search /></el-icon>
            <span>校园卡查询</span>
          </el-menu-item>
          <el-menu-item index="/recharge">
            <el-icon><Money /></el-icon>
            <span>余额充值</span>
          </el-menu-item>
          <el-menu-item index="/consume">
            <el-icon><ShoppingCart /></el-icon>
            <span>校园卡消费</span>
          </el-menu-item>
          <el-menu-item index="/delete">
            <el-icon><Delete /></el-icon>
            <span>校园卡注销</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header height="60px">
          <div class="header-content">
            <h2>校园卡管理系统</h2>
          </div>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Plus,
  Search,
  Money,
  ShoppingCart,
  Delete
} from '@element-plus/icons-vue'

const router = useRouter()
const activeIndex = ref(router.currentRoute.value.path)

const handleSelect = (key) => {
  activeIndex.value = key
}
</script>

<style scoped>
.common-layout {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #545c64;
}

.el-menu {
  height: 100%;
  border-right: none;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 20px;
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  color: #303133;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}

.el-menu-vertical {
  background-color: #545c64;
}

.el-menu-item {
  color: #fff;
}

.el-menu-item:hover {
  background-color: #434a50;
}

.el-menu-item.is-active {
  background-color: #434a50;
}
</style>
