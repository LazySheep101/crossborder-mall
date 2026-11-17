<template>
  <div>
    <h2>限时秒杀</h2>
    <div class="grid">
      <article v-for="a in list" :key="a.id" class="card item" @click="$router.push(`/seckills/${a.id}`)">
        <div class="thumb" :style="{ backgroundImage: `url(${a.productImage || ''})` }" />
        <h3>{{ a.title }}</h3>
        <p class="muted">{{ a.productName }} · {{ a.specDesc }}</p>
        <p>
          <span class="price">¥{{ Number(a.seckillPrice).toFixed(2) }}</span>
          <span class="muted origin">¥{{ Number(a.originPrice || 0).toFixed(2) }}</span>
        </p>
        <p class="muted">剩余 {{ a.stock }}</p>
      </article>
    </div>
    <el-empty v-if="!list.length" description="暂无进行中的秒杀" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { listSeckillActivities } from '@/api/seckill'

const list = ref([])
onMounted(async () => {
  list.value = (await listSeckillActivities()) || []
})
</script>

<style scoped lang="scss">
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}
.item { cursor: pointer; }
.thumb {
  height: 140px;
  border-radius: 10px;
  background: #f0ebe4 center/cover no-repeat;
  margin-bottom: 8px;
}
.price { color: var(--brand-dark); font-weight: 800; font-size: 18px; margin-right: 8px; }
.origin { text-decoration: line-through; }
h3 { margin: 0 0 4px; font-size: 15px; }
</style>
