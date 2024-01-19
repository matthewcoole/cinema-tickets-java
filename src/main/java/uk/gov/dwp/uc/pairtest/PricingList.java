package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

/**
 * Simple class to store and return prices. For testing purposes (should really be some kind of service or connection to a price database).
 */
public class PricingList {

    private static Map<TicketTypeRequest.Type, Integer> prices = new HashMap<>();

    static {
        prices.put(ADULT, 20);
        prices.put(CHILD, 10);
        prices.put(INFANT, 0);
    }

    public static Integer getPrice(TicketTypeRequest.Type ticketType) {
        return prices.get(ticketType);
    }
}
