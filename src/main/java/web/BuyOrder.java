package web;

import domain.Cupcake;
import domain.Order;
import domain.User;
import exeptions.LoginSampleException;
import exeptions.ValidationError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class BuyOrder extends Command {
    @Override
    String execute(HttpServletRequest request, HttpServletResponse response) throws LoginSampleException {

        HttpSession session = request.getSession();

        //Double
        String userBank = request.getParameter("userbank");
        //double
        String orderPrice = request.getParameter("orderprice");
        String userid = request.getParameter("userid");
        double calculateOrder = 0;
        int newUserId = 0;

        try {
            newUserId = Integer.parseInt(userid);
            calculateOrder = calculateOrder(userBank, orderPrice);
        } catch (ValidationError validationError) {
            validationError.printStackTrace();
        }

        Order order = api.getOrderFacade().getOrderById(newUserId);
        List<Cupcake> allCupcakes = api.getCupcakeFacade().getCupcakesInOrder(order);

        if(calculateOrder < 0) {
            request.setAttribute("nomoney", "Du har ikke nok penge på din konto");
            request.setAttribute("orderprice", order.getPrice());
            request.setAttribute("allcupcakes", allCupcakes);
            request.setAttribute("preorder", order);
            return "Kurv";
        } else {
            User user = api.getUserFacade().findUser(newUserId);
            api.getUserFacade().updateUserBank(newUserId, calculateOrder);
            Order orderPurchased = api.getOrderFacade().orderPurchased(order);

            session.setAttribute("bank", user.getBank());
            request.setAttribute("order", orderPurchased);
            request.setAttribute("user", user);
            request.setAttribute("orderprice", orderPurchased.getPrice());
            request.setAttribute("allcupcakes", allCupcakes);
        }

        return "Kvitering";
    }




    private double calculateOrder(String userBank, String orderPrice) throws ValidationError {
        double calculateOrder = 0;
        double newUserBank = 0;
        double newOrderPrice = 0;

        try {
            newUserBank = Double.parseDouble(userBank);
            newOrderPrice = Double.parseDouble(orderPrice);
        } catch (NumberFormatException e) {
            throw new ValidationError(e.toString());
        }

        calculateOrder = newUserBank - newOrderPrice;

        return calculateOrder;
    }
}
