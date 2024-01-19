import org.junit.Test;

import static org.junit.Assert.assertSame;
import static uk.gov.dwp.uc.pairtest.PricingList.getPrice;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class PricingListTests {
    @Test
    public void testGetPriceAdultTicket() {
        assertSame(getPrice(ADULT), 20);
    }

    @Test
    public void testGetPriceChildTicket() {
        assertSame(getPrice(CHILD), 10);
    }

    @Test
    public void testGetPriceInfantTicket() {
        assertSame(getPrice(INFANT), 0);
    }
}
