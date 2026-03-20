import 'package:go_router/go_router.dart';

import '../auth/screens/login_screen.dart';
import '../auth/screens/role_selection_screen.dart';
import '../auth/screens/seller_info_screen.dart';
import '../auth/screens/splash_screen.dart';
import '../../buyer/screens/buyer_home_screen.dart';
import '../../buyer/screens/buyer_my_tab_screen.dart';
import '../../buyer/screens/buyer_notifications_tab_screen.dart';
import '../../buyer/screens/buyer_requests_tab_screen.dart';
import '../../buyer/screens/buyer_reservations_tab_screen.dart';
import '../../buyer/screens/buyer_shell_screen.dart';
import '../../buyer/screens/payment_screen.dart';
import '../../buyer/screens/proposal_detail_screen.dart';
import '../../buyer/screens/request_detail_screen.dart';
import '../../buyer/screens/request_done_screen.dart';
import '../../buyer/screens/request_step1_screen.dart';
import '../../buyer/screens/request_step2_screen.dart';
import '../../buyer/screens/request_step3_delivery_screen.dart';
import '../../buyer/screens/request_step3_pickup_screen.dart';
import '../../buyer/screens/request_step4_delivery_screen.dart';
import '../../buyer/screens/request_step4_pickup_screen.dart';
import '../../buyer/screens/buyer_reservation_detail_screen.dart';
import '../../buyer/screens/reservation_done_screen.dart';
import '../../seller/screens/seller_home_screen.dart';
import '../../seller/screens/seller_my_tab_screen.dart';
import '../../seller/screens/seller_proposal_done_screen.dart';
import '../../seller/screens/seller_proposal_step1_screen.dart';
import '../../seller/screens/seller_proposal_step2_pickup_screen.dart';
import '../../seller/screens/seller_request_detail_screen.dart';
import '../../seller/screens/seller_requests_tab_screen.dart';
import '../../seller/screens/seller_notifications_screen.dart';
import '../../seller/screens/seller_reservation_detail_screen.dart';
import '../../seller/screens/seller_shell_screen.dart';
import '../../seller/screens/seller_reservations_tab_screen.dart';

final appRouter = GoRouter(
  initialLocation: '/splash',
  routes: [
    // ========== AUTH ==========
    GoRoute(path: '/splash', builder: (_, __) => const SplashScreen()),
    GoRoute(path: '/login', builder: (_, __) => const LoginScreen()),
    GoRoute(path: '/auth/role', builder: (_, __) => const RoleSelectionScreen()),
    GoRoute(path: '/auth/seller-info', builder: (_, __) => const SellerInfoScreen()),

    // ========== BUYER SHELL ==========
    StatefulShellRoute.indexedStack(
      builder: (context, state, navigationShell) {
        return BuyerShellScreen(
          currentIndex: navigationShell.currentIndex,
          onTabChanged: (index) => navigationShell.goBranch(index),
          child: navigationShell,
        );
      },
      branches: [
        StatefulShellBranch(routes: [
          GoRoute(path: '/buyer/home', builder: (_, __) => const BuyerHomeScreen()),
        ]),
        StatefulShellBranch(routes: [
          GoRoute(path: '/buyer/requests', builder: (_, __) => const BuyerRequestsTabScreen()),
        ]),
        StatefulShellBranch(routes: [
          GoRoute(path: '/buyer/reservations', builder: (_, __) => const BuyerReservationsTabScreen()),
        ]),
        StatefulShellBranch(routes: [
          GoRoute(path: '/buyer/my', builder: (_, __) => const BuyerMyTabScreen()),
        ]),
      ],
    ),

    // ========== BUYER DETAIL ROUTES ==========
    GoRoute(path: '/buyer/request/step1', builder: (_, __) => const RequestStep1Screen()),
    GoRoute(path: '/buyer/request/step2', builder: (_, __) => const RequestStep2Screen()),
    GoRoute(path: '/buyer/request/step3/pickup', builder: (_, __) => const RequestStep3PickupScreen()),
    GoRoute(path: '/buyer/request/step3/delivery', builder: (_, __) => const RequestStep3DeliveryScreen()),
    GoRoute(path: '/buyer/request/step4/pickup', builder: (_, __) => const RequestStep4PickupScreen()),
    GoRoute(path: '/buyer/request/step4/delivery', builder: (_, __) => const RequestStep4DeliveryScreen()),
    GoRoute(path: '/buyer/request/done', builder: (_, __) => const RequestDoneScreen()),
    GoRoute(
      path: '/buyer/requests/:id',
      builder: (_, state) => RequestDetailScreen(requestId: int.parse(state.pathParameters['id']!)),
    ),
    GoRoute(
      path: '/buyer/proposals/:id',
      builder: (_, state) => ProposalDetailScreen(proposalId: int.parse(state.pathParameters['id']!)),
    ),
    GoRoute(
      path: '/buyer/proposals/:id/pay',
      builder: (_, state) => PaymentScreen(proposalId: int.parse(state.pathParameters['id']!)),
    ),
    GoRoute(path: '/buyer/notifications', builder: (_, __) => const BuyerNotificationsTabScreen()),
    GoRoute(
      path: '/buyer/reservations/:id',
      builder: (_, state) => BuyerReservationDetailScreen(reservationId: int.parse(state.pathParameters['id']!)),
    ),
    GoRoute(
      path: '/buyer/reservations/:id/done',
      builder: (_, state) => ReservationDoneScreen(reservationId: int.parse(state.pathParameters['id']!)),
    ),

    // ========== SELLER SHELL ==========
    StatefulShellRoute.indexedStack(
      builder: (context, state, navigationShell) {
        return SellerShellScreen(
          currentIndex: navigationShell.currentIndex,
          onTabChanged: (index) => navigationShell.goBranch(index),
          child: navigationShell,
        );
      },
      branches: [
        StatefulShellBranch(routes: [
          GoRoute(path: '/seller/home', builder: (_, __) => const SellerHomeScreen()),
        ]),
        StatefulShellBranch(routes: [
          GoRoute(path: '/seller/requests', builder: (_, __) => const SellerRequestsTabScreen()),
        ]),
        StatefulShellBranch(routes: [
          GoRoute(path: '/seller/reservations', builder: (_, __) => const SellerReservationsTabScreen()),
        ]),
        StatefulShellBranch(routes: [
          GoRoute(path: '/seller/my', builder: (_, __) => const SellerMyTabScreen()),
        ]),
      ],
    ),

    // ========== SELLER DETAIL ROUTES ==========
    GoRoute(
      path: '/seller/requests/:id',
      builder: (_, state) => SellerRequestDetailScreen(requestId: int.parse(state.pathParameters['id']!)),
    ),
    GoRoute(
      path: '/seller/proposals/new/step1',
      builder: (_, state) => SellerProposalStep1Screen(
        requestId: int.parse(state.uri.queryParameters['requestId']!),
      ),
    ),
    GoRoute(path: '/seller/proposals/new/step2/pickup', builder: (_, __) => const SellerProposalStep2PickupScreen()),
    GoRoute(path: '/seller/proposals/done', builder: (_, __) => const SellerProposalDoneScreen()),
    GoRoute(path: '/seller/notifications', builder: (_, __) => const SellerNotificationsScreen()),
    GoRoute(
      path: '/seller/reservations/:id',
      builder: (_, state) => SellerReservationDetailScreen(reservationId: int.parse(state.pathParameters['id']!)),
    ),
  ],
);
