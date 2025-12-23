# Calendar for Android - 产品报告

## 1. 产品功能介绍
本项目是一款基于 Android 平台的现代化日历应用，旨在提供简洁高效的时间管理体验。

*   **多视图日历展示**：支持年视图、月视图、周视图和日视图，满足不同粒度的时间查看需求。
*   **日程管理**：用户可以创建、编辑和删除日程事件。
*   **详细的日程属性**：支持设置标题、描述、开始/结束时间、地点。
*   **重复规则**：支持设置日程的重复模式（无、每天、每周、每月、每年）。
*   **定时提醒**：支持自定义提醒时间（如提前 N 分钟），通过系统通知提醒用户。
*   **数据导入导出**：支持以 JSON 格式导出和导入日程数据，方便数据备份与迁移。

## 2. 程序概要设计
本程序采用标准的 Android 应用开发规范，基于 MVVM 架构模式进行设计。

*   **UI 层**：完全采用 **Jetpack Compose** 构建，遵循 Material Design 3 设计规范，提供流畅的动画和现代化的视觉体验。主要界面包括底部导航栏、各时间维度的日历视图以及日程编辑页。
*   **逻辑层**：使用 `CalendarViewModel` 作为 UI 与数据之间的桥梁，负责处理业务逻辑（如日期计算、事件过滤）和状态管理。
*   **数据层**：使用 **Room** 数据库框架进行本地数据持久化，保证数据的安全存储和快速访问。
*   **提醒服务**：利用 Android 原生 `AlarmManager` 和 `BroadcastReceiver` 实现精准的定时提醒功能。

## 3. 软件架构图
本项目采用经典的 MVVM (Model-View-ViewModel) 架构，确保代码的解耦和可维护性。

```mermaid
graph TD
    User((用户)) --> View[UI Layer<br/>(Screens & Components)]
    View -->|观察状态/触发事件| ViewModel[CalendarViewModel]
    
    subgraph Data Layer
        ViewModel -->|调用| Repository[CalendarRepository]
        Repository -->|CRUD操作| DAO[CalendarEventDao]
        DAO -->|SQL查询| DB[(Room Database)]
    end
    
    subgraph System Services
        ViewModel -->|设置提醒| ReminderManager
        ReminderManager -->|调度| AlarmManager[Android AlarmManager]
        AlarmManager -->|广播| AlarmReceiver
        AlarmReceiver -->|显示通知| Notification[系统通知]
    end
```

## 4. 技术亮点及其实现原理

### 4.1 全面采用 Jetpack Compose
*   **亮点**：抛弃了传统的 XML 布局方式，使用声明式的 Kotlin 代码构建 UI。这使得界面开发更加直观，代码量更少，且更容易实现复杂的交互和动画。
*   **实现**：通过 `@Composable` 函数定义 UI 组件（如 `MonthScreen`, `DayScreen`），利用 `StateFlow` 驱动界面刷新，实现了“状态即 UI”的设计理念。

### 4.2 基于 Room 的高效数据持久化
*   **亮点**：使用 Google 官方推荐的 Room 数据库，提供了类型安全的 SQL 查询和编译时检查。
*   **实现**：定义 `CalendarEvent` 实体类并标注 `@Entity`，通过 `CalendarEventDao` 接口定义数据库操作，Room 库自动生成底层实现代码，简化了数据库访问逻辑。

### 4.3 精准的日程提醒机制
*   **亮点**：即使应用处于后台或被杀死，依然能够准时提醒用户。
*   **实现**：封装 `ReminderManager` 类，使用 `AlarmManager.setExactAndAllowWhileIdle` 接口设置高精度的 RTC 唤醒闹钟。配合 `AlarmReceiver` 广播接收器，在预定时间触发系统通知。

### 4.4 响应式编程模型
*   **亮点**：数据变化能实时反映在 UI 上，无需手动刷新。
*   **实现**：利用 Kotlin Coroutines 和 Flow。Repository 层返回 `Flow<List<CalendarEvent>>`，ViewModel 将其转换为 `StateFlow` 暴露给 UI。当数据库发生变更时，流会自动发射新数据，Compose UI 随之重组。

### 4.5 灵活的重复规则引擎
*   **亮点**：支持多种复杂的重复日程设置。
*   **实现**：通过 `RepeatMode` 枚举（DAILY, WEEKLY 等）定义重复类型。在逻辑层处理日期匹配算法，判断某一天是否包含重复事件，从而在日历视图中正确渲染。

