import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/theme/colors.dart';
import '../../core/theme/radius.dart';
import '../../core/theme/typography.dart';
import '../providers/request_form_provider.dart';
import '../widgets/common/app_nav_bar.dart';
import '../widgets/common/bottom_cta_button.dart';
import '../widgets/common/step_progress_bar.dart';
import '../widgets/request/fulfillment_toggle.dart';
import '../widgets/request/address_search_field.dart';
import '../widgets/request/date_picker_field.dart';

class RequestStep3DeliveryScreen extends ConsumerWidget {
  const RequestStep3DeliveryScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final form = ref.watch(requestFormProvider);
    final notifier = ref.read(requestFormProvider.notifier);

    return Scaffold(
      backgroundColor: creamColor,
      body: SafeArea(
        child: Column(
          children: [
            const AppNavBar(title: '꽃다발 요청하기'),
            const StepProgressBar(totalSteps: 4, currentStep: 3),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(18, 8, 18, 16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    FulfillmentToggle(
                      isPickup: false,
                      onPickup: () {
                        notifier.setFulfillmentType('PICKUP');
                        context.pushReplacement('/buyer/request/step3/pickup');
                      },
                      onDelivery: () {
                        notifier.setFulfillmentType('DELIVERY');
                      },
                    ),
                    const SizedBox(height: 20),
                    Text(
                      '🏠 배송지 주소',
                      style: AppTypography.body(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 8),
                    AddressSearchField(
                      value: form.placeAddressText,
                      placeholder: '주소를 검색해주세요',
                      onSelected: (addr, lat, lng) {
                        notifier.setPlace(addr, lat, lng);
                      },
                    ),
                    const SizedBox(height: 6),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 13,
                      ),
                      decoration: BoxDecoration(
                        color: creamColor,
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: borderColor, width: 1.5),
                      ),
                      child: Text(
                        '상세 주소 (동·호수 등)',
                        style: AppTypography.body(fontSize: 12, color: ink30),
                      ),
                    ),
                    const SizedBox(height: 8),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: creamColor,
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: borderColor),
                      ),
                      alignment: Alignment.center,
                      child: Text(
                        '📍 지도 미리보기',
                        style: AppTypography.body(fontSize: 12, color: ink30),
                      ),
                    ),
                    const SizedBox(height: 20),
                    Text(
                      '📅 배송 날짜',
                      style: AppTypography.body(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 8),
                    DatePickerField(
                      value: form.fulfillmentDate,
                      onSelected: notifier.setFulfillmentDate,
                    ),
                    const SizedBox(height: 16),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: const Color(0xFFE8F0EC),
                        borderRadius: kBorderRadiusSm,
                        border: Border.all(color: const Color(0xFFB8CFC4)),
                      ),
                      child: Text(
                        '✅ 배송지 반경 2km 내 플로리스트에게 요청서가 전달돼요.',
                        style: AppTypography.body(
                          fontSize: 11,
                          color: const Color(0xFF5A7A68),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            BottomCtaButton(
              label: '다음 — 시간대 선택',
              enabled: form.placeAddressText != null &&
                  form.fulfillmentDate != null,
              onPressed: () {
                notifier.setFulfillmentType('DELIVERY');
                context.push('/buyer/request/step4/delivery');
              },
            ),
          ],
        ),
      ),
    );
  }
}
