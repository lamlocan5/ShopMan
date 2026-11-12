package view.khachhang;
import model.Account6;

public class CustomerHomeUI6 {
    private final Account6 sessionAccount;
    private String nextView;
    public CustomerHomeUI6(Account6 sessionAccount) {

        this.sessionAccount = sessionAccount;
    }
    public void openSearch() { nextView = "ProductSearchUI6.jsp"; }
    public String getNextView() { return nextView; }
    public Account6 getSessionAccount() { return sessionAccount; }
}
