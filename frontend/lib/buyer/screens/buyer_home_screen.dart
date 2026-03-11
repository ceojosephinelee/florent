import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../widgets/home/active_request_section.dart';
import '../widgets/home/buyer_top_bar.dart';
import '../widgets/home/greeting_section.dart';
import '../widgets/home/how_it_works_section.dart';

class BuyerHomeScreen extends ConsumerWidget {
  const BuyerHomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      backgroundColor: creamColor,
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SafeArea(bottom: false, child: BuyerTopBar()),
            GreetingSection(
              onCreateRequest: () => context.push('/buyer/request/step1'),
            ),
            const Divider(),
            const ActiveRequestSection(),
            const Divider(),
            const HowItWorksSection(),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }
}
