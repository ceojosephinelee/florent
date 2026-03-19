import 'dart:async';

import 'package:dio/dio.dart';
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
import '../widgets/request/date_picker_field.dart';

class RequestStep3PickupScreen extends ConsumerStatefulWidget {
  const RequestStep3PickupScreen({super.key});

  @override
  ConsumerState<RequestStep3PickupScreen> createState() =>
      _RequestStep3PickupScreenState();
}

class _RequestStep3PickupScreenState
    extends ConsumerState<RequestStep3PickupScreen> {
  final _neighborhoodController = TextEditingController();
  final _dio = Dio(BaseOptions(
    baseUrl: const String.fromEnvironment(
      'API_BASE_URL',
      defaultValue: 'http://localhost:8080/api/v1',
    ),
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
  ));
  Timer? _debounce;

  @override
  void initState() {
    super.initState();
    final form = ref.read(requestFormProvider);
    if (form.placeAddressText != null) {
      _neighborhoodController.text = form.placeAddressText!;
    }
  }

  @override
  void dispose() {
    _neighborhoodController.dispose();
    _debounce?.cancel();
    _dio.close();
    super.dispose();
  }

  void _onNeighborhoodChanged(String value) {
    _debounce?.cancel();
    final text = value.trim();
    if (text.isEmpty) {
      ref.read(requestFormProvider.notifier).setPlace('', 0, 0);
      return;
    }
    _debounce = Timer(const Duration(milliseconds: 600), () {
      final current = _neighborhoodController.text.trim();
      if (current.isEmpty) return;
      _resolveCoordinates(current);
    });
  }

  Future<void> _resolveCoordinates(String query) async {
    try {
      final response = await _dio.get(
        '/addresses/search',
        queryParameters: {'query': query},
      );
      final data = response.data['data'] as List? ?? [];
      final notifier = ref.read(requestFormProvider.notifier);
      if (data.isNotEmpty) {
        final first = data[0];
        notifier.setPlace(
          query,
          (first['lat'] as num?)?.toDouble() ?? 0,
          (first['lng'] as num?)?.toDouble() ?? 0,
        );
      } else {
        notifier.setPlace(query, 0, 0);
      }
    } catch (_) {
      ref.read(requestFormProvider.notifier).setPlace(query, 0, 0);
    }
  }

  @override
  Widget build(BuildContext context) {
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
                      isPickup: true,
                      onPickup: () {
                        notifier.setFulfillmentType('PICKUP');
                      },
                      onDelivery: () {
                        notifier.setFulfillmentType('DELIVERY');
                        context.pushReplacement('/buyer/request/step3/delivery');
                      },
                    ),
                    const SizedBox(height: 20),
                    Text(
                      '📍 픽업 동네',
                      style: AppTypography.body(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      '픽업 가능한 동네를 입력해주세요.\n입력하신 동네 근처 플로리스트들에게 요청서가 전달돼요.',
                      style: AppTypography.body(
                        fontSize: 12,
                        color: ink60,
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 8),
                    TextField(
                      controller: _neighborhoodController,
                      style: AppTypography.body(fontSize: 13),
                      decoration: InputDecoration(
                        hintText: '예: 강남구 역삼동, 마포구 합정동',
                        hintStyle:
                            AppTypography.body(fontSize: 13, color: ink30),
                        filled: true,
                        fillColor: creamColor,
                        border: OutlineInputBorder(
                          borderRadius: kBorderRadiusSm,
                          borderSide:
                              BorderSide(color: borderColor, width: 1.5),
                        ),
                        enabledBorder: OutlineInputBorder(
                          borderRadius: kBorderRadiusSm,
                          borderSide:
                              BorderSide(color: borderColor, width: 1.5),
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: kBorderRadiusSm,
                          borderSide: BorderSide(color: ink30, width: 1.5),
                        ),
                        contentPadding: const EdgeInsets.symmetric(
                            horizontal: 12, vertical: 13),
                      ),
                      onChanged: _onNeighborhoodChanged,
                    ),
                    const SizedBox(height: 20),
                    Text(
                      '📅 픽업 날짜',
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
                  ],
                ),
              ),
            ),
            BottomCtaButton(
              label: '다음 — 시간 선택',
              enabled: form.placeAddressText != null &&
                  form.placeAddressText!.isNotEmpty &&
                  form.fulfillmentDate != null,
              onPressed: () {
                notifier.setFulfillmentType('PICKUP');
                context.push('/buyer/request/step4/pickup');
              },
            ),
          ],
        ),
      ),
    );
  }
}

