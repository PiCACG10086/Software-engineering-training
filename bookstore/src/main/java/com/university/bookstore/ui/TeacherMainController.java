package com.university.bookstore.ui;

import com.university.bookstore.model.*;
import com.university.bookstore.service.*;
import com.university.bookstore.service.impl.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
/**
 * 教师主界面控制器
 */
public class TeacherMainController extends BaseController implements Initializable {
    
    // 图书浏览相关控件
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> bookTitleColumn;
    @FXML
    private TableColumn<Book, String> bookAuthorColumn;
    @FXML
    private TableColumn<Book, String> bookPublisherColumn;
    @FXML
    private TableColumn<Book, BigDecimal> bookPriceColumn;
    @FXML
    private TableColumn<Book, Integer> bookStockColumn;
    @FXML
    private TextField bookSearchField;
    @FXML
    private Button bookSearchButton;
    @FXML
    private Button viewBookDetailsButton;
    

    
    // 主界面控件
    @FXML
    private TabPane mainTabPane;
    
    // 学生管理相关控件
    @FXML
    private TableView<User> studentTable;
    @FXML
    private TableColumn<User, String> studentNameColumn;
    @FXML
    private TableColumn<User, String> studentIdColumn;
    @FXML
    private TableColumn<User, String> studentUsernameColumn;
    @FXML
    private TableColumn<User, String> studentCreateTimeColumn;
    @FXML
    private TextField studentSearchField;
    @FXML
    private Button studentSearchButton;
    @FXML
    private Button viewStudentOrdersButton;
    
    // 订单查看相关控件
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, String> orderNumberColumn;
    @FXML
    private TableColumn<Order, String> orderStudentColumn;
    @FXML
    private TableColumn<Order, BigDecimal> orderTotalColumn;
    @FXML
    private TableColumn<Order, String> orderStatusColumn;
    @FXML
    private TableColumn<Order, String> orderDateColumn;
    @FXML
    private TextField orderSearchField;
    @FXML
    private Button orderSearchButton;
    @FXML
    private Button refreshOrderButton;
    @FXML
    private Button viewOrderDetailsButton;
    @FXML
    private ComboBox<String> teacherOrderStatusFilter;
    

    
    // 个人信息相关控件
    @FXML
    private Label teacherUsernameLabel;
    @FXML
    private Label teacherNameLabel;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button logoutButton;
    
    // 服务类
    private BookService bookService;
    private OrderService orderService;
    private UserService userService;
    
    // 自动刷新定时器
    private Timer autoRefreshTimer;
    private static final int AUTO_REFRESH_INTERVAL = 5000; // 5秒自动刷新
    

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化服务
        bookService = new BookServiceImpl();
        orderService = new OrderServiceImpl();
        userService = new UserServiceImpl();
        
        // 初始化表格
        initializeBookTable();

        initializeStudentTable();
        initializeOrderTable();
        
        // 初始化控件
        initializeControls();
        
        // 加载数据
        loadAllData();
        
        // 启动自动刷新
        startAutoRefresh();
    }
    
    @Override
    protected void onUserSet() {
        if (currentUser != null) {
            teacherUsernameLabel.setText(currentUser.getUsername());
            teacherNameLabel.setText(currentUser.getName());
        }
    }
    
    /**
     * 初始化图书表格
     */
    private void initializeBookTable() {
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookPublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        bookPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        bookStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        // 设置价格格式
        bookPriceColumn.setCellFactory(column -> new TableCell<Book, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("¥%.2f", price));
                }
            }
        });
        
        // 设置表格选择模式为单选
        bookTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    

    
    /**
     * 初始化学生表格
     */
    private void initializeStudentTable() {
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        studentCreateTimeColumn.setCellValueFactory(cellData -> {
            Date createTime = cellData.getValue().getCreateTime();
            if (createTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return new SimpleStringProperty(sdf.format(createTime));
            }
            return new SimpleStringProperty("");
        });
        
        // 设置表格选择模式为单选
        studentTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    /**
     * 初始化订单表格
     */
    private void initializeOrderTable() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderStudentColumn.setCellValueFactory(cellData -> {
            User student = userService.getUserById(cellData.getValue().getStudentId());
            return new SimpleStringProperty(student != null ? student.getName() : "未知");
        });
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderStatusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatOrderStatus(cellData.getValue().getStatus())));
        orderDateColumn.setCellValueFactory(cellData -> {
            Date createTime = cellData.getValue().getCreateTime();
            if (createTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return new SimpleStringProperty(sdf.format(createTime));
            }
            return new SimpleStringProperty("");
        });
        
        // 设置价格格式
        orderTotalColumn.setCellFactory(column -> new TableCell<Order, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("¥%.2f", price));
                }
            }
        });
        
        // 设置表格选择模式为单选
        orderTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    /**
     * 初始化控件
     */
    private void initializeControls() {
        // 初始化订单状态筛选器
        teacherOrderStatusFilter.setItems(FXCollections.observableArrayList(
            "全部订单", "待支付", "已支付", "已确认", "已发货", "已完成", "已取消"
        ));
        teacherOrderStatusFilter.setValue("全部订单");
        
        // 为订单状态筛选器添加选择监听器，实现自动筛选
        teacherOrderStatusFilter.setOnAction(event -> refreshOrdersWithCurrentFilter());
        
        // 图书浏览按钮事件
        bookSearchButton.setOnAction(event -> handleBookSearch());
        viewBookDetailsButton.setOnAction(event -> handleViewBookDetails());
        
        // 图书推荐按钮事件

        
        // 学生管理按钮事件
        studentSearchButton.setOnAction(event -> handleStudentSearch());
        viewStudentOrdersButton.setOnAction(event -> handleViewStudentOrders());
        
        // 订单查看按钮事件
        orderSearchButton.setOnAction(event -> handleOrderSearch());
        refreshOrderButton.setOnAction(event -> loadOrders());
        viewOrderDetailsButton.setOnAction(event -> handleViewOrderDetails());
        
        // 其他按钮事件
        changePasswordButton.setOnAction(event -> handleChangePassword());
        logoutButton.setOnAction(event -> handleLogout());
        
        // 设置回车搜索
        bookSearchField.setOnAction(event -> handleBookSearch());

        studentSearchField.setOnAction(event -> handleStudentSearch());
        orderSearchField.setOnAction(event -> handleOrderSearch());
        
        // 初始化推荐表单状态

    }
    
    /**
     * 加载所有数据
     */
    private void loadAllData() {
        loadBooks();

        loadStudents();
        loadOrders();
    }
    
    /**
     * 加载图书数据
     */
    private void loadBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            bookTable.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载图书数据失败：" + e.getMessage());
        }
    }
    

    
    /**
     * 加载学生数据
     */
    private void loadStudents() {
        try {
            List<User> students = userService.getAllStudents();
            studentTable.setItems(FXCollections.observableArrayList(students));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载学生数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 加载订单数据
     */
    private void loadOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            orderTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载订单数据失败：" + e.getMessage());
        }
    }
    

    
    // 图书浏览相关方法
    
    @FXML
    private void handleBookSearch() {
        String keyword = bookSearchField.getText().trim();
        try {
            List<Book> books;
            if (keyword.isEmpty()) {
                books = bookService.getAllBooks();
            } else {
                books = bookService.searchBooks(keyword);
            }
            bookTable.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索图书失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewBookDetails() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarningAlert("请选择图书", "请先选择要查看的图书");
            return;
        }
        
        showBookDetailsDialog(selectedBook);
    }
    

    
    // 学生管理相关方法
    
    @FXML
    private void handleStudentSearch() {
        String keyword = studentSearchField.getText().trim();
        try {
            List<User> students;
            if (keyword.isEmpty()) {
                students = userService.getAllStudents();
            } else {
                // 根据姓名或学号搜索
                students = userService.getAllStudents().stream()
                    .filter(student -> student.getName().contains(keyword) || 
                                     (student.getStudentId() != null && student.getStudentId().contains(keyword)))
                    .collect(java.util.stream.Collectors.toList());
            }
            studentTable.setItems(FXCollections.observableArrayList(students));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索学生失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewStudentOrders() {
        User selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showWarningAlert("请选择学生", "请先选择要查看订单的学生");
            return;
        }
        
        try {
            List<Order> studentOrders = orderService.getOrdersByStudentId(selectedStudent.getId());
            orderTable.setItems(FXCollections.observableArrayList(studentOrders));
            // 切换到订单查看Tab（索引为2：图书浏览=0，学生管理=1，订单查看=2）
            mainTabPane.getSelectionModel().select(2);
            showInfoAlert("查看成功", "已显示学生 " + selectedStudent.getName() + " 的订单");
        } catch (Exception e) {
            showErrorAlert("查看失败", "查看学生订单失败：" + e.getMessage());
        }
    }
    
    // 订单查看相关方法
    
    @FXML
    private void handleOrderSearch() {
        String keyword = orderSearchField.getText().trim();
        try {
            List<Order> orders;
            if (keyword.isEmpty()) {
                orders = orderService.getAllOrders();
            } else {
                // 先尝试按订单号搜索
                Order orderByNumber = orderService.getOrderByOrderNumber(keyword);
                if (orderByNumber != null) {
                    orders = Arrays.asList(orderByNumber);
                } else {
                    // 按学生姓名搜索
                    orders = new ArrayList<>();
                    List<Order> allOrders = orderService.getAllOrders();
                    for (Order order : allOrders) {
                        User student = userService.getUserById(order.getStudentId());
                        if (student != null && student.getName().contains(keyword)) {
                            orders.add(order);
                        }
                    }
                }
            }
            orderTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据当前筛选条件刷新订单数据
     */
    private void refreshOrdersWithCurrentFilter() {
        try {
            String selectedStatus = teacherOrderStatusFilter.getValue();
            if (selectedStatus == null) {
                selectedStatus = "全部订单";
            }
            
            List<Order> filteredOrders;
            
            if ("全部订单".equals(selectedStatus)) {
                filteredOrders = orderService.getAllOrders();
            } else {
                // 将字符串转换为OrderStatus枚举
                Order.OrderStatus status;
                switch (selectedStatus) {
                    case "待支付":
                        status = Order.OrderStatus.PENDING;
                        break;
                    case "已支付":
                        status = Order.OrderStatus.PAID;
                        break;
                    case "已确认":
                        status = Order.OrderStatus.CONFIRMED;
                        break;
                    case "已发货":
                        status = Order.OrderStatus.SHIPPED;
                        break;
                    case "已完成":
                        status = Order.OrderStatus.COMPLETED;
                        break;
                    case "已取消":
                        status = Order.OrderStatus.CANCELLED;
                        break;
                    default:
                        status = Order.OrderStatus.PENDING;
                        break;
                }
                filteredOrders = orderService.getOrdersByStatus(status);
            }
            
            orderTable.setItems(FXCollections.observableArrayList(filteredOrders));
            
        } catch (Exception e) {
            showErrorAlert("错误", "筛选订单失败: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewOrderDetails() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要查看的订单");
            return;
        }
        
        try {
            List<OrderDetail> details = orderService.getOrderDetails(selectedOrder.getId());
            showOrderDetailsDialog(selectedOrder, details);
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载订单详情失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleChangePassword() {
        showChangePasswordDialog();
    }
    
    /**
     * 显示修改密码对话框
     */
    private void showChangePasswordDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/change_password_dialog.fxml"));
            VBox dialogContent = loader.load();
            
            // 获取对话框中的控件
            PasswordField currentPasswordField = (PasswordField) dialogContent.lookup("#currentPasswordField");
            PasswordField newPasswordField = (PasswordField) dialogContent.lookup("#newPasswordField");
            PasswordField confirmPasswordField = (PasswordField) dialogContent.lookup("#confirmPasswordField");
            Button changeButton = (Button) dialogContent.lookup("#changeButton");
            Button cancelButton = (Button) dialogContent.lookup("#cancelButton");
            
            // 创建对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("修改密码");
            dialog.getDialogPane().setContent(dialogContent);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            
            // 隐藏默认的取消按钮
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
            
            // 设置对话框结果转换器
            dialog.setResultConverter(dialogButton -> {
                return null;
            });
            
            // 修改按钮事件
            changeButton.setOnAction(e -> {
                String currentPassword = currentPasswordField.getText().trim();
                String newPassword = newPasswordField.getText().trim();
                String confirmPassword = confirmPasswordField.getText().trim();
                
                if (validatePasswordForm(currentPassword, newPassword, confirmPassword)) {
                    try {
                        if (userService.changePassword(currentUser.getId(), currentPassword, newPassword)) {
                            // 先关闭修改密码对话框
                            dialog.close();
                            
                            // 显示成功提示
                            showInfoAlert("修改成功", "密码已修改成功，请重新登录");
                            
                            // 跳转到登录界面并关闭当前主窗口
                            try {
                                // 清除当前用户信息
                                currentUser = null;
                                
                                // 获取主窗口（通过主界面控件获取）
                                Stage mainStage = (Stage) bookTable.getScene().getWindow();
                                
                                // 加载登录界面
                                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                                Scene loginScene = new Scene(loginLoader.load(), 400, 300);
                                
                                // 切换到登录界面
                                mainStage.setScene(loginScene);
                                mainStage.setTitle("用户登录");
                                mainStage.centerOnScreen();
                                
                            } catch (IOException ioEx) {
                                showErrorAlert("跳转失败", "跳转到登录界面失败：" + ioEx.getMessage());
                                ioEx.printStackTrace();
                            }
                        } else {
                            showErrorAlert("修改失败", "当前密码不正确或修改失败，请重试");
                        }
                    } catch (Exception ex) {
                        showErrorAlert("修改失败", "修改密码失败：" + ex.getMessage());
                    }
                }
            });
            
            // 取消按钮事件
            cancelButton.setOnAction(e -> dialog.close());
            
            dialog.showAndWait();
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载修改密码对话框失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证密码表单
     */
    private boolean validatePasswordForm(String currentPassword, String newPassword, String confirmPassword) {
        if (!validateNotEmpty(currentPassword, "当前密码")) {
            return false;
        }
        if (!validateNotEmpty(newPassword, "新密码")) {
            return false;
        }
        if (!validateNotEmpty(confirmPassword, "确认密码")) {
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            showWarningAlert("验证失败", "新密码和确认密码不一致");
            return false;
        }
        if (newPassword.length() < 6) {
            showWarningAlert("验证失败", "新密码长度不能少于6位");
            return false;
        }
        return true;
    }
    

    
    // 辅助方法
    

    
    /**
     * 显示图书详情对话框
     */
    private void showBookDetailsDialog(Book book) {
        StringBuilder sb = new StringBuilder();
        sb.append("ISBN：").append(book.getIsbn()).append("\n");
        sb.append("书名：").append(book.getTitle()).append("\n");
        sb.append("作者：").append(book.getAuthor()).append("\n");
        sb.append("出版社：").append(book.getPublisher()).append("\n");
        sb.append("价格：¥").append(book.getPrice()).append("\n");
        sb.append("库存：").append(book.getStock()).append("\n");
        if (book.getDescription() != null && !book.getDescription().trim().isEmpty()) {
            sb.append("描述：").append(book.getDescription()).append("\n");
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("图书详情");
        alert.setHeaderText(book.getTitle());
        alert.setContentText(sb.toString());
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }
    
    /**
     * 显示订单详情对话框
     * @param order 订单对象
     * @param details 订单详情列表
     */
    private void showOrderDetailsDialog(Order order, List<OrderDetail> details) {
        StringBuilder sb = new StringBuilder();
        sb.append("订单号：").append(order.getOrderNumber()).append("\n");
        
        User student = userService.getUserById(order.getStudentId());
        sb.append("学生姓名：").append(student != null ? student.getName() : "未知").append("\n");
        sb.append("学生用户名：").append(student != null ? student.getUsername() : "未知").append("\n");
        if (student != null && student.getStudentId() != null) {
            sb.append("学号：").append(student.getStudentId()).append("\n");
        }
        
        sb.append("订单状态：").append(formatOrderStatus(order.getStatus())).append("\n");
        sb.append("下单时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime())).append("\n");
        sb.append("订单总价：¥").append(order.getTotalPrice()).append("\n\n");
        sb.append("购买的图书详情：\n");
        sb.append("=".repeat(50)).append("\n");
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            Book book = bookService.getBookById(detail.getBookId());
            if (book != null) {
                sb.append("第").append(i + 1).append("本图书：\n");
                sb.append("  书名：").append(book.getTitle()).append("\n");
                sb.append("  作者：").append(book.getAuthor()).append("\n");
                sb.append("  出版社：").append(book.getPublisher()).append("\n");
                sb.append("  ISBN：").append(book.getIsbn()).append("\n");
                sb.append("  单价：¥").append(detail.getPrice()).append("\n");
                sb.append("  购买数量：").append(detail.getQuantity()).append("本\n");
                sb.append("  小计：¥").append(detail.getSubtotal()).append("\n");
                if (i < details.size() - 1) {
                    sb.append("-".repeat(30)).append("\n");
                }
                totalAmount = totalAmount.add(detail.getSubtotal());
            }
        }
        
        sb.append("=".repeat(50)).append("\n");
        sb.append("总计：¥").append(totalAmount).append("\n");
        sb.append("共购买 ").append(details.size()).append(" 种图书\n");
        
        // 创建自定义对话框以支持滚动
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("订单详情");
        dialog.setHeaderText("订单 " + order.getOrderNumber() + " 的详细信息");
        
        // 创建文本区域并设置为只读
        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(60);
        
        // 创建滚动面板
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefSize(650, 500);
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }
    
    /**
     * 处理刷新订单事件
     */
    private void handleRefreshOrder() {
        loadOrders();
        orderTable.refresh();
        showInfoAlert("刷新成功", "订单列表已刷新");
    }
    
    /**
     * 启动自动刷新功能
     */
    private void startAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
        }
        
        autoRefreshTimer = new Timer(true); // 设置为守护线程
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 在JavaFX应用线程中执行刷新操作
                Platform.runLater(() -> {
                    try {
                        // 刷新图书和学生数据
                        loadBooks();
                        loadStudents();
                        // 对于订单数据，保持当前筛选状态
                        refreshOrdersWithCurrentFilter();
                    } catch (Exception e) {
                        // 静默处理异常，避免干扰用户操作
                        System.err.println("自动刷新失败: " + e.getMessage());
                    }
                });
            }
        }, AUTO_REFRESH_INTERVAL, AUTO_REFRESH_INTERVAL);
    }
    
    /**
     * 停止自动刷新功能
     */
    private void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
            autoRefreshTimer = null;
        }
    }
    
    /**
     * 重启自动刷新功能
     */
    public void restartAutoRefresh() {
        stopAutoRefresh();
        startAutoRefresh();
    }
    
    /**
     * 处理退出登录事件
     */
    private void handleLogout() {
        handleLogout(logoutButton);
    }
}