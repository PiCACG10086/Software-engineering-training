package com.university.bookstore.ui;

import com.university.bookstore.model.*;
import com.university.bookstore.service.*;
import com.university.bookstore.service.impl.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private Button viewOrderDetailsButton;
    @FXML
    private Button confirmOrderButton;
    @FXML
    private Button shipOrderButton;
    @FXML
    private Button cancelOrderButton;
    
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
    
    // 统计信息相关控件
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label pendingOrdersLabel;
    @FXML
    private Label todayOrdersLabel;
    @FXML
    private Label lowStockBooksLabel;
    
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
    
    // 当前编辑的图书
    private Book currentEditingBook;
    
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
    }
    
    @Override
    protected void onUserSet() {
        if (currentUser != null) {
            adminUsernameLabel.setText(currentUser.getUsername());
            adminNameLabel.setText(currentUser.getName());
            loadStatistics();
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
        
        // 订单管理按钮事件
        orderSearchButton.setOnAction(event -> handleOrderSearch());
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
        logoutButton.setOnAction(event -> handleLogout());
        
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
        loadStatistics();
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
    
    /**
     * 加载用户数据
     */
    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载用户数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 加载统计信息
     */
    private void loadStatistics() {
        try {
            totalBooksLabel.setText(String.valueOf(bookService.getTotalBookCount()));
            totalUsersLabel.setText(String.valueOf(userService.getAllUsers().size()));
            totalOrdersLabel.setText(String.valueOf(orderService.getTotalOrderCount()));
            pendingOrdersLabel.setText(String.valueOf(orderService.getPendingOrderCount()));
            todayOrdersLabel.setText(String.valueOf(orderService.getTodayOrderCount()));
            lowStockBooksLabel.setText(String.valueOf(bookService.getLowStockBooks(10).size()));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载统计信息失败：" + e.getMessage());
        }
    }
    
    // 图书管理相关方法
    
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
        try {
            List<Order> orders;
            if (keyword.isEmpty()) {
                orders = orderService.getAllOrders();
            } else {
                // 根据订单号搜索
                Order order = orderService.getOrderByOrderNumber(keyword);
                orders = order != null ? Arrays.asList(order) : new ArrayList<>();
            }
            orderTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索订单失败：" + e.getMessage());
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
    private void handleConfirmOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要确认的订单");
            return;
        }
        
        if (selectedOrder.getStatus() != Order.OrderStatus.PENDING) {
            showWarningAlert("无法确认", "只能确认待处理状态的订单");
            return;
        }
        
        if (showConfirmDialog("确认订单", "确定要确认订单 " + selectedOrder.getOrderNumber() + " 吗？")) {
            try {
                if (orderService.confirmOrder(selectedOrder.getId())) {
                    loadOrders();
                    loadStatistics();
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
                    loadOrders();
                    loadStatistics();
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
                    loadOrders();
                    loadStatistics();
                    showInfoAlert("取消成功", "订单已取消");
                } else {
                    showErrorAlert("取消失败", "取消订单失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("取消失败", "取消订单失败：" + e.getMessage());
            }
        }
    }
    
    // 用户管理相关方法
    
    @FXML
    private void handleUserSearch() {
        String keyword = userSearchField.getText().trim();
        try {
            List<User> users;
            if (keyword.isEmpty()) {
                users = userService.getAllUsers();
            } else {
                User user = userService.getUserByUsername(keyword);
                users = user != null ? Arrays.asList(user) : new ArrayList<>();
            }
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索用户失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleAddUser() {
        showInfoAlert("功能提示", "添加用户功能待实现");
    }
    
    @FXML
    private void handleEditUser() {
        showInfoAlert("功能提示", "编辑用户功能待实现");
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
                    loadStatistics();
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
        showInfoAlert("功能提示", "修改密码功能待实现");
    }
    
    @FXML
    private void handleLogout() {
        if (showConfirmDialog("确认退出", "确定要退出登录吗？")) {
            showInfoAlert("退出登录", "退出登录功能待实现");
        }
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
     */
    private void showOrderDetailsDialog(Order order, List<OrderDetail> details) {
        StringBuilder sb = new StringBuilder();
        sb.append("订单号：").append(order.getOrderNumber()).append("\n");
        
        User student = userService.getUserById(order.getStudentId());
        sb.append("学生：").append(student != null ? student.getName() : "未知").append("\n");
        
        sb.append("订单状态：").append(formatOrderStatus(order.getStatus())).append("\n");
        sb.append("下单时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime())).append("\n");
        sb.append("订单总价：¥").append(order.getTotalPrice()).append("\n\n");
        sb.append("订单详情：\n");
        
        for (OrderDetail detail : details) {
            Book book = bookService.getBookById(detail.getBookId());
            if (book != null) {
                sb.append("- ").append(book.getTitle())
                  .append(" × ").append(detail.getQuantity())
                  .append(" = ¥").append(detail.getSubtotal()).append("\n");
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("订单详情");
        alert.setHeaderText(null);
        alert.setContentText(sb.toString());
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }
}