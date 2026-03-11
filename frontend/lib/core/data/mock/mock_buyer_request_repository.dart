import '../../models/buyer_request.dart';
import '../buyer_request_repository.dart';
import 'mock_data.dart';

class MockBuyerRequestRepository implements BuyerRequestRepository {
  @override
  Future<List<BuyerRequestSummary>> getActiveRequests() async {
    await Future.delayed(const Duration(milliseconds: 300));
    return mockActiveRequests;
  }
}
