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
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
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
 * 管理员主界面控制器
 */
public class AdminMainController extends BaseController implements Initializable {
    
    // 图书管理相关控件
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
    private Button addBookButton;
    @FXML
    private Button editBookButton;
    @FXML
    private Button deleteBookButton;
    
    // 图书编辑表单
    @FXML
    private TextField isbnField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publisherField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button saveBookButton;
    @FXML
    private Button cancelBookButton;
    
    // 订单管理相关控件
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
    private Button confirmOrderButton;
    @FXML
    private Button shipOrderButton;
    @FXML
    private Button cancelOrderButton;
    @FXML
    private Button deleteOrderButton;
    @FXML
    private ComboBox<String> adminOrderStatusFilter;
    @FXML
    private Button adminFilterOrderButton;
    
    // 用户管理相关控件
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> userUsernameColumn;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userRoleColumn;
    @FXML
    private TableColumn<User, String> userStudentIdColumn;
    @FXML
    private TableColumn<User, String> userCreateTimeColumn;
    @FXML
    private TextField userSearchField;
    @FXML
    private Button userSearchButton;
    @FXML
    private Button addUserButton;
    @FXML
    private Button editUserButton;
    @FXML
    private Button deleteUserButton;
    @FXML
    private Button resetPasswordButton;
    

    
    // 个人信息相关控件
    @FXML
    private Label adminUsernameLabel;
    @FXML
    private Label adminNameLabel;
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
    
    // 当前编辑的图书
    private Book currentEditingBook;
    
    // 分页相关字段
    private static final int ITEMS_PER_PAGE = 10; // 每页显示的项目数
    private int currentBookPage = 1; // 当前图书页码
    private int totalBookPages = 1; // 图书总页数
    private int currentOrderPage = 1; // 当前订单页码
    private int totalOrderPages = 1; // 订单总页数
    private int currentUserPage = 1; // 当前用户页码
    private int totalUserPages = 1; // 用户总页数
    private String currentBookSearchKeyword = ""; // 当前图书搜索关键词
    private String currentOrderSearchKeyword = ""; // 当前订单搜索关键词
    private String currentUserSearchKeyword = ""; // 当前用户搜索关键词
    private String currentOrderFilter = "全部订单"; // 当前订单筛选条件
    
    // 分页控件（需要在FXML中添加）
    @FXML
    private Pagination bookPagination;
    @FXML
    private Pagination orderPagination;
    @FXML
    private Pagination userPagination;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化服务
        bookService = new BookServiceImpl();
        orderService = new OrderServiceImpl();
        userService = new UserServiceImpl();
        
        // 初始化表格
        initializeBookTable();
        initializeOrderTable();
        initializeUserTable();
        
        // 初始化控件
        initializeControls();
        
        // 加载数据
        loadAllData();
        
        // 初始化分页控件事件
        initializePagination();
        
        // 启动自动刷新
        startAutoRefresh();
    }
    
    @Override
    protected void onUserSet() {
        if (currentUser != null) {
            adminUsernameLabel.setText(currentUser.getUsername());
            adminNameLabel.setText(currentUser.getName());
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
        
        // 设置选择事件
        bookTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadBookToForm(newSelection);
                }
            });
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
    }
    
    /**
     * 初始化用户表格
     */
    private void initializeUserTable() {
        userUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userRoleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatUserRole(cellData.getValue().getRole())));
        userStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        userCreateTimeColumn.setCellValueFactory(cellData -> {
            Date createTime = cellData.getValue().getCreateTime();
            if (createTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return new SimpleStringProperty(sdf.format(createTime));
            }
            return new SimpleStringProperty("");
        });
    }
    
    /**
     * 初始化控件
     */
    private void initializeControls() {
        // 图书管理按钮事件
        bookSearchButton.setOnAction(event -> handleBookSearch());
        addBookButton.setOnAction(event -> handleAddBook());
        editBookButton.setOnAction(event -> handleEditBook());
        deleteBookButton.setOnAction(event -> handleDeleteBook());
        saveBookButton.setOnAction(event -> handleSaveBook());
        cancelBookButton.setOnAction(event -> handleCancelBookEdit());
        
        // 初始化管理员订单状态筛选下拉框
        adminOrderStatusFilter.setItems(FXCollections.observableArrayList(
            "全部订单", "待支付", "已支付", "已发货", "已完成", "已取消"
        ));
        adminOrderStatusFilter.setValue("全部订单");
        
        // 订单管理按钮事件
        orderSearchButton.setOnAction(event -> handleOrderSearch());
        // 移除筛选按钮事件，改为下拉列表值变化监听
        // adminFilterOrderButton.setOnAction(event -> handleAdminFilterOrders());
        
        // 添加下拉列表值变化监听器，实现自动筛选
        adminOrderStatusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshOrdersWithCurrentFilter();
            }
        });
        viewOrderDetailsButton.setOnAction(event -> handleViewOrderDetails());
        confirmOrderButton.setOnAction(event -> handleConfirmOrder());
        shipOrderButton.setOnAction(event -> handleShipOrder());
        cancelOrderButton.setOnAction(event -> handleCancelOrder());
        
        // 用户管理按钮事件
        userSearchButton.setOnAction(event -> handleUserSearch());
        addUserButton.setOnAction(event -> handleAddUser());
        editUserButton.setOnAction(event -> handleEditUser());
        deleteUserButton.setOnAction(event -> handleDeleteUser());
        resetPasswordButton.setOnAction(event -> handleResetPassword());
        
        // 其他按钮事件
        changePasswordButton.setOnAction(event -> handleChangePassword());
        logoutButton.setOnAction(event -> handleLogout(logoutButton));
        
        // 设置回车搜索
        bookSearchField.setOnAction(event -> handleBookSearch());
        orderSearchField.setOnAction(event -> handleOrderSearch());
        userSearchField.setOnAction(event -> handleUserSearch());
        
        // 初始化表单状态
        setBookFormEditable(false);
    }
    
    /**
     * 加载所有数据
     */
    private void loadAllData() {
        loadBooks();
        loadOrders();
        loadUsers();
    }
    
    /**
     * 初始化分页控件
     */
    private void initializePagination() {
        // 初始化图书分页控件
        if (bookPagination != null) {
            bookPagination.setPageFactory(pageIndex -> {
                currentBookPage = pageIndex + 1;
                loadBooksWithPagination();
                return bookTable;
            });
        }
        
        // 初始化订单分页控件
        if (orderPagination != null) {
            orderPagination.setPageFactory(pageIndex -> {
                currentOrderPage = pageIndex + 1;
                loadOrdersWithPagination();
                return orderTable;
            });
        }
        
        // 初始化用户分页控件
        if (userPagination != null) {
            userPagination.setPageFactory(pageIndex -> {
                currentUserPage = pageIndex + 1;
                loadUsersWithPagination();
                return userTable;
            });
        }
    }
    
    /**
     * 加载图书数据
     */
    private void loadBooks() {
        currentBookSearchKeyword = "";
        currentBookPage = 1;
        loadBooksWithPagination();
    }
    
    /**
     * 分页加载图书数据
     */
    private void loadBooksWithPagination() {
        try {
            List<Book> allBooks;
            if (currentBookSearchKeyword.isEmpty()) {
                allBooks = bookService.getAllBooks();
            } else {
                allBooks = bookService.searchBooks(currentBookSearchKeyword);
            }
            
            // 计算总页数
            totalBookPages = (int) Math.ceil((double) allBooks.size() / ITEMS_PER_PAGE);
            if (totalBookPages == 0) totalBookPages = 1;
            
            // 获取当前页的数据
            int startIndex = (currentBookPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allBooks.size());
            
            List<Book> pageBooks = allBooks.subList(startIndex, endIndex);
            bookTable.setItems(FXCollections.observableArrayList(pageBooks));
            
            // 更新分页控件
            if (bookPagination != null) {
                bookPagination.setPageCount(totalBookPages);
                bookPagination.setCurrentPageIndex(currentBookPage - 1);
            }
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载图书数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 加载订单数据
     */
    private void loadOrders() {
        // 重置筛选条件为"全部订单"
        adminOrderStatusFilter.setValue("全部订单");
        currentOrderFilter = "全部订单";
        currentOrderPage = 1;
        loadOrdersWithPagination();
    }
    
    /**
     * 分页加载订单数据
     */
    private void loadOrdersWithPagination() {
        try {
            List<Order> allOrders;
            if (!currentOrderSearchKeyword.isEmpty()) {
                // 根据订单号搜索
                Order order = orderService.getOrderByOrderNumber(currentOrderSearchKeyword);
                allOrders = order != null ? Arrays.asList(order) : new ArrayList<>();
            } else if ("全部订单".equals(currentOrderFilter)) {
                allOrders = orderService.getAllOrders();
            } else {
                // 将字符串转换为OrderStatus枚举
                Order.OrderStatus status;
                switch (currentOrderFilter) {
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
                allOrders = orderService.getOrdersByStatus(status);
            }
            
            // 计算总页数
            totalOrderPages = (int) Math.ceil((double) allOrders.size() / ITEMS_PER_PAGE);
            if (totalOrderPages == 0) totalOrderPages = 1;
            
            // 获取当前页的数据
            int startIndex = (currentOrderPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allOrders.size());
            
            List<Order> pageOrders = allOrders.subList(startIndex, endIndex);
            orderTable.setItems(FXCollections.observableArrayList(pageOrders));
            
            // 更新分页控件
            if (orderPagination != null) {
                orderPagination.setPageCount(totalOrderPages);
                orderPagination.setCurrentPageIndex(currentOrderPage - 1);
            }
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载订单数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 加载用户数据
     */
    private void loadUsers() {
        currentUserSearchKeyword = "";
        currentUserPage = 1;
        loadUsersWithPagination();
    }
    
    /**
     * 分页加载用户数据
     */
    private void loadUsersWithPagination() {
        try {
            List<User> allUsers;
            if (currentUserSearchKeyword.isEmpty()) {
                allUsers = userService.getAllUsers();
            } else {
                // 根据用户名搜索
                User user = userService.getUserByUsername(currentUserSearchKeyword);
                allUsers = user != null ? Arrays.asList(user) : new ArrayList<>();
            }
            
            // 计算总页数
            totalUserPages = (int) Math.ceil((double) allUsers.size() / ITEMS_PER_PAGE);
            if (totalUserPages == 0) totalUserPages = 1;
            
            // 获取当前页的数据
            int startIndex = (currentUserPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allUsers.size());
            
            List<User> pageUsers = allUsers.subList(startIndex, endIndex);
            userTable.setItems(FXCollections.observableArrayList(pageUsers));
            
            // 更新分页控件
            if (userPagination != null) {
                userPagination.setPageCount(totalUserPages);
                userPagination.setCurrentPageIndex(currentUserPage - 1);
            }
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载用户数据失败：" + e.getMessage());
        }
    }
    

    
    // 图书管理相关方法
    
    @FXML
    private void handleBookSearch() {
        String keyword = bookSearchField.getText().trim();
        currentBookSearchKeyword = keyword;
        currentBookPage = 1;
        loadBooksWithPagination();
    }
    
    @FXML
    private void handleAddBook() {
        clearBookForm();
        setBookFormEditable(true);
        currentEditingBook = null;
    }
    
    @FXML
    private void handleEditBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarningAlert("请选择图书", "请先选择要编辑的图书");
            return;
        }
        
        setBookFormEditable(true);
        currentEditingBook = selectedBook;
    }
    
    @FXML
    private void handleDeleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarningAlert("请选择图书", "请先选择要删除的图书");
            return;
        }
        
        if (showConfirmDialog("确认删除", "确定要删除图书《" + selectedBook.getTitle() + "》吗？")) {
            try {
                if (bookService.deleteBook(selectedBook.getId())) {
                    loadBooks();
                    clearBookForm();
                    showInfoAlert("删除成功", "图书已删除");
                } else {
                    showErrorAlert("删除失败", "删除图书失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("删除失败", "删除图书失败：" + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSaveBook() {
        if (!validateBookForm()) {
            return;
        }
        
        try {
            Book book = createBookFromForm();
            boolean success;
            
            if (currentEditingBook == null) {
                // 添加新图书
                success = bookService.addBook(book);
            } else {
                // 更新图书
                book.setId(currentEditingBook.getId());
                success = bookService.updateBook(book);
            }
            
            if (success) {
                loadBooks();
                setBookFormEditable(false);
                showInfoAlert("保存成功", "图书信息已保存");
            } else {
                showErrorAlert("保存失败", "保存图书信息失败，请重试");
            }
        } catch (Exception e) {
            showErrorAlert("保存失败", "保存图书信息失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelBookEdit() {
        setBookFormEditable(false);
        if (currentEditingBook != null) {
            loadBookToForm(currentEditingBook);
        } else {
            clearBookForm();
        }
    }
    
    // 订单管理相关方法
    
    @FXML
    private void handleOrderSearch() {
        String keyword = orderSearchField.getText().trim();
        currentOrderSearchKeyword = keyword;
        currentOrderPage = 1;
        loadOrdersWithPagination();
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
    private void handleConfirmOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要确认的订单");
            return;
        }
        
        if (selectedOrder.getStatus() != Order.OrderStatus.PENDING && selectedOrder.getStatus() != Order.OrderStatus.PAID) {
            showWarningAlert("无法确认", "只能确认待支付或已支付状态的订单");
            return;
        }
        
        if (showConfirmDialog("确认订单", "确定要确认订单 " + selectedOrder.getOrderNumber() + " 吗？")) {
            try {
                if (orderService.confirmOrder(selectedOrder.getId())) {
                    // 清空当前选择
                    orderTable.getSelectionModel().clearSelection();
                    // 重新加载订单数据
                    loadOrders();
                    // 刷新表格显示
                    orderTable.refresh();
                    showInfoAlert("确认成功", "订单已确认");
                } else {
                    showErrorAlert("确认失败", "确认订单失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("确认失败", "确认订单失败：" + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleShipOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要发货的订单");
            return;
        }
        
        if (!orderService.canShipOrder(selectedOrder.getId())) {
            showWarningAlert("无法发货", "该订单当前状态无法发货");
            return;
        }
        
        if (showConfirmDialog("确认发货", "确定要发货订单 " + selectedOrder.getOrderNumber() + " 吗？")) {
            try {
                if (orderService.shipOrder(selectedOrder.getId())) {
                    // 清空当前选择
                    orderTable.getSelectionModel().clearSelection();
                    // 重新加载订单数据
                    loadOrders();
                    // 刷新表格显示
                    orderTable.refresh();
                    showInfoAlert("发货成功", "订单已发货");
                } else {
                    showErrorAlert("发货失败", "发货失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("发货失败", "发货失败：" + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancelOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要取消的订单");
            return;
        }
        
        if (!orderService.canCancelOrder(selectedOrder.getId())) {
            showWarningAlert("无法取消", "该订单当前状态无法取消");
            return;
        }
        
        if (showConfirmDialog("确认取消", "确定要取消订单 " + selectedOrder.getOrderNumber() + " 吗？")) {
            try {
                if (orderService.cancelOrder(selectedOrder.getId(), currentUser.getId())) {
                    // 清空当前选择
                    orderTable.getSelectionModel().clearSelection();
                    // 重新加载订单数据
                    loadOrders();
                    // 刷新表格显示
                    orderTable.refresh();
                    showInfoAlert("取消成功", "订单已取消");
                } else {
                    showErrorAlert("取消失败", "取消订单失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("取消失败", "取消订单失败：" + e.getMessage());
            }
        }
    }
    
    /**
     * 刷新订单列表
     */
    @FXML
    private void handleRefreshOrder() {
        try {
            // 清空当前选择
            orderTable.getSelectionModel().clearSelection();
            // 重新加载订单数据
            loadOrders();
            // 刷新表格显示
            orderTable.refresh();
            showInfoAlert("刷新成功", "订单列表已刷新");
        } catch (Exception e) {
            showErrorAlert("刷新失败", "刷新订单列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除订单
     */
    @FXML
    private void handleDeleteOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要删除的订单");
            return;
        }
        
        // 只允许删除已取消或已完成的订单
        if (selectedOrder.getStatus() != Order.OrderStatus.CANCELLED && 
            selectedOrder.getStatus() != Order.OrderStatus.COMPLETED) {
            showWarningAlert("无法删除", "只能删除已取消或已完成的订单");
            return;
        }
        
        if (showConfirmDialog("确认删除", "确定要删除订单 " + selectedOrder.getOrderNumber() + " 吗？\n注意：删除后无法恢复！")) {
            try {
                if (orderService.deleteOrder(selectedOrder.getId())) {
                    loadOrders();
                    showInfoAlert("删除成功", "订单已删除");
                } else {
                    showErrorAlert("删除失败", "删除订单失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("删除失败", "删除订单失败：" + e.getMessage());
            }
        }
    }
    
    // 用户管理相关方法
    
    @FXML
    private void handleUserSearch() {
        String keyword = userSearchField.getText().trim();
        currentUserSearchKeyword = keyword;
        currentUserPage = 1;
        loadUsersWithPagination();
    }
    
    @FXML
    private void handleAddUser() {
        showUserDialog(null);
    }
    
    @FXML
    private void handleEditUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showWarningAlert("请选择用户", "请先选择要编辑的用户");
            return;
        }
        showUserDialog(selectedUser);
    }
    
    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showWarningAlert("请选择用户", "请先选择要删除的用户");
            return;
        }
        
        if (selectedUser.getId().equals(currentUser.getId())) {
            showWarningAlert("无法删除", "不能删除当前登录的用户");
            return;
        }
        
        if (showConfirmDialog("确认删除", "确定要删除用户 " + selectedUser.getUsername() + " 吗？")) {
            try {
                if (userService.deleteUser(selectedUser.getId())) {
                    loadUsers();
                    showInfoAlert("删除成功", "用户已删除");
                } else {
                    showErrorAlert("删除失败", "删除用户失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("删除失败", "删除用户失败：" + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleResetPassword() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showWarningAlert("请选择用户", "请先选择要重置密码的用户");
            return;
        }
        
        if (showConfirmDialog("确认重置", "确定要重置用户 " + selectedUser.getUsername() + " 的密码为 '123456' 吗？")) {
            try {
                if (userService.resetPassword(selectedUser.getId(), "123456")) {
                    showInfoAlert("重置成功", "密码已重置为 '123456'");
                } else {
                    showErrorAlert("重置失败", "重置密码失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("重置失败", "重置密码失败：" + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleChangePassword() {
        showChangePasswordDialog();
    }
    

    
    // 辅助方法
    
    /**
     * 设置图书表单是否可编辑
     */
    private void setBookFormEditable(boolean editable) {
        isbnField.setEditable(editable);
        titleField.setEditable(editable);
        authorField.setEditable(editable);
        publisherField.setEditable(editable);
        priceField.setEditable(editable);
        stockField.setEditable(editable);
        descriptionArea.setEditable(editable);
        
        saveBookButton.setVisible(editable);
        cancelBookButton.setVisible(editable);
    }
    
    /**
     * 清空图书表单
     */
    private void clearBookForm() {
        isbnField.clear();
        titleField.clear();
        authorField.clear();
        publisherField.clear();
        priceField.clear();
        stockField.clear();
        descriptionArea.clear();
    }
    
    /**
     * 将图书信息加载到表单
     */
    private void loadBookToForm(Book book) {
        if (book != null) {
            isbnField.setText(book.getIsbn());
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            publisherField.setText(book.getPublisher());
            priceField.setText(book.getPrice().toString());
            stockField.setText(book.getStock().toString());
            descriptionArea.setText(book.getDescription());
        }
    }
    
    /**
     * 验证图书表单
     */
    private boolean validateBookForm() {
        return validateNotEmpty(isbnField.getText(), "ISBN") &&
               validateNotEmpty(titleField.getText(), "书名") &&
               validateNotEmpty(authorField.getText(), "作者") &&
               validateNotEmpty(publisherField.getText(), "出版社") &&
               validateNumber(priceField.getText(), "价格") &&
               validatePositiveInteger(stockField.getText(), "库存");
    }
    
    /**
     * 从表单创建图书对象
     */
    private Book createBookFromForm() {
        Book book = new Book();
        book.setIsbn(isbnField.getText().trim());
        book.setTitle(titleField.getText().trim());
        book.setAuthor(authorField.getText().trim());
        book.setPublisher(publisherField.getText().trim());
        book.setPrice(new BigDecimal(priceField.getText().trim()));
        book.setStock(Integer.parseInt(stockField.getText().trim()));
        book.setDescription(descriptionArea.getText().trim());
        return book;
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
        
        // 调试信息：显示订单详情数量
        sb.append("订单详情数量：").append(details.size()).append("\n");
        
        if (details.isEmpty()) {
            sb.append("暂无订单详情数据\n");
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            // 调试信息：显示订单详情基本信息
            sb.append("订单详情ID：").append(detail.getId()).append("，图书ID：").append(detail.getBookId()).append("\n");
            
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
     * 显示用户添加/编辑对话框
     */
    private void showUserDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_dialog.fxml"));
            VBox dialogContent = loader.load();
            
            // 获取对话框中的控件
            TextField usernameField = (TextField) dialogContent.lookup("#usernameField");
            TextField nameField = (TextField) dialogContent.lookup("#nameField");
            ComboBox<String> roleComboBox = (ComboBox<String>) dialogContent.lookup("#roleComboBox");
            TextField studentIdField = (TextField) dialogContent.lookup("#studentIdField");
            PasswordField passwordField = (PasswordField) dialogContent.lookup("#passwordField");
            Button saveButton = (Button) dialogContent.lookup("#saveButton");
            Button cancelButton = (Button) dialogContent.lookup("#cancelButton");
            
            // 初始化角色选择框
            roleComboBox.getItems().addAll("STUDENT", "TEACHER", "ADMIN");
            
            // 创建对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(user == null ? "添加用户" : "编辑用户");
            dialog.getDialogPane().setContent(dialogContent);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
            
            // 隐藏默认的取消按钮
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
            
            // 设置对话框结果转换器
            dialog.setResultConverter(dialogButton -> {
                return null;
            });
            
            // 如果是编辑模式，填充现有数据
            if (user != null) {
                usernameField.setText(user.getUsername());
                usernameField.setEditable(false); // 编辑时不允许修改用户名
                nameField.setText(user.getName());
                roleComboBox.setValue(user.getRole().toString());
                studentIdField.setText(user.getStudentId());
                passwordField.setPromptText("留空则不修改密码");
                
                // 如果编辑的是当前登录的管理员，不允许修改角色
                if (currentUser != null && currentUser.getId().equals(user.getId()) && 
                    currentUser.getRole() == User.UserRole.ADMIN) {
                    roleComboBox.setDisable(true);
                    // 只允许管理员修改自己的密码
                    nameField.setEditable(false);
                    studentIdField.setEditable(false);
                }
            } else {
                roleComboBox.setValue("STUDENT"); // 默认选择学生角色
            }
            
            // 角色变化时控制学号字段的可见性
            roleComboBox.setOnAction(e -> {
                boolean isStudent = "STUDENT".equals(roleComboBox.getValue());
                studentIdField.setDisable(!isStudent);
                if (!isStudent) {
                    studentIdField.clear();
                }
            });
            
            // 初始化学号字段状态
            boolean isStudent = "STUDENT".equals(roleComboBox.getValue());
            studentIdField.setDisable(!isStudent);
            
            // 保存按钮事件
            saveButton.setOnAction(e -> {
                if (validateUserForm(usernameField, nameField, roleComboBox, studentIdField, passwordField, user == null)) {
                    try {
                        User newUser = createUserFromForm(usernameField, nameField, roleComboBox, studentIdField, passwordField);
                        boolean success;
                        
                        if (user == null) {
                            // 添加新用户
                            success = userService.addUser(newUser);
                        } else {
                            // 更新用户
                            newUser.setId(user.getId());
                            newUser.setUsername(user.getUsername()); // 保持原用户名
                            if (passwordField.getText().trim().isEmpty()) {
                                newUser.setPassword(user.getPassword()); // 保持原密码
                            }
                            success = userService.updateUser(newUser);
                        }
                        
                        if (success) {
                            loadUsers();
                            dialog.close();
                            showInfoAlert("保存成功", "用户信息已保存");
                        } else {
                            showErrorAlert("保存失败", "保存用户信息失败，请重试");
                        }
                    } catch (Exception ex) {
                        showErrorAlert("保存失败", "保存用户信息失败：" + ex.getMessage());
                    }
                }
            });
            
            // 取消按钮事件
            cancelButton.setOnAction(e -> dialog.close());
            
            dialog.showAndWait();
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载用户对话框失败：" + e.getMessage());
        }
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
     * 验证用户表单
     */
    private boolean validateUserForm(TextField usernameField, TextField nameField, ComboBox<String> roleComboBox, 
                                   TextField studentIdField, PasswordField passwordField, boolean isNewUser) {
        if (!validateNotEmpty(usernameField.getText(), "用户名")) {
            return false;
        }
        if (!validateNotEmpty(nameField.getText(), "姓名")) {
            return false;
        }
        if (roleComboBox.getValue() == null) {
            showWarningAlert("验证失败", "请选择用户角色");
            return false;
        }
        if ("STUDENT".equals(roleComboBox.getValue()) && !validateNotEmpty(studentIdField.getText(), "学号")) {
            return false;
        }
        if (isNewUser && !validateNotEmpty(passwordField.getText(), "密码")) {
            return false;
        }
        // 验证密码长度（新用户或修改密码时）
        if (isNewUser || !passwordField.getText().trim().isEmpty()) {
            String password = passwordField.getText().trim();
            if (password.length() < 6) {
                showWarningAlert("验证失败", "密码长度不能少于6位");
                return false;
            }
            if (password.length() > 20) {
                showWarningAlert("验证失败", "密码长度不能超过20位");
                return false;
            }
        }
        return true;
    }
    
    /**
     * 从表单创建用户对象
     */
    private User createUserFromForm(TextField usernameField, TextField nameField, ComboBox<String> roleComboBox, 
                                  TextField studentIdField, PasswordField passwordField) {
        User user = new User();
        user.setUsername(usernameField.getText().trim());
        user.setName(nameField.getText().trim());
        user.setRole(User.UserRole.valueOf(roleComboBox.getValue()));
        user.setStudentId("STUDENT".equals(roleComboBox.getValue()) ? studentIdField.getText().trim() : null);
        if (!passwordField.getText().trim().isEmpty()) {
            user.setPassword(passwordField.getText().trim());
        }
        return user;
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
                        // 刷新图书、订单和用户数据，保持当前页和搜索/筛选条件
                        loadBooksWithPagination();
                        loadOrdersWithPagination();
                        loadUsersWithPagination();
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
     * 处理管理员订单状态筛选
     */
    @FXML
    private void handleAdminFilterOrders() {
        refreshOrdersWithCurrentFilter();
    }
    
    /**
     * 根据当前筛选条件刷新订单数据
     */
    private void refreshOrdersWithCurrentFilter() {
        String selectedStatus = adminOrderStatusFilter.getValue();
        if (selectedStatus == null) {
            selectedStatus = "全部订单";
        }
        currentOrderFilter = selectedStatus;
        currentOrderPage = 1;
        loadOrdersWithPagination();
    }
    
}