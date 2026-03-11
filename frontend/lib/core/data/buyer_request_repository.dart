import '../models/buyer_request.dart';

abstract class BuyerRequestRepository {
  Future<List<BuyerRequestSummary>> getActiveRequests();
}
