import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';

class AddressSearchField extends StatelessWidget {
  const AddressSearchField({
    super.key,
    this.value,
    required this.placeholder,
    required this.onSelected,
  });

  final String? value;
  final String placeholder;
  final void Function(String address, double lat, double lng) onSelected;

  @override
  Widget build(BuildContext context) {
    final hasValue = value != null && value!.isNotEmpty;

    return GestureDetector(
      onTap: () => _openSearch(context),
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 13),
        decoration: BoxDecoration(
          color: creamColor,
          borderRadius: kBorderRadiusSm,
          border: Border.all(
            color: hasValue ? ink30 : borderColor,
            width: 1.5,
          ),
        ),
        child: Row(
          children: [
            Expanded(
              child: Text(
                hasValue ? value! : placeholder,
                style: AppTypography.body(
                  fontSize: 13,
                  color: hasValue ? inkColor : ink30,
                ),
              ),
            ),
            Icon(Icons.search, size: 18, color: ink30),
          ],
        ),
      ),
    );
  }

  Future<void> _openSearch(BuildContext context) async {
    final result = await showModalBottomSheet<_AddressResult>(
      context: context,
      isScrollControlled: true,
      backgroundColor: whiteColor,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (_) => const _AddressSearchSheet(),
    );
    if (result != null) {
      onSelected(result.address, result.lat, result.lng);
    }
  }
}

class _AddressResult {
  final String address;
  final double lat;
  final double lng;

  const _AddressResult(this.address, this.lat, this.lng);
}

class _AddressSearchSheet extends StatefulWidget {
  const _AddressSearchSheet();

  @override
  State<_AddressSearchSheet> createState() => _AddressSearchSheetState();
}

class _AddressSearchSheetState extends State<_AddressSearchSheet> {
  final _controller = TextEditingController();
  final _dio = Dio();
  Timer? _debounce;
  List<_AddressResult> _results = [];
  bool _loading = false;

  static const _restKey =
      String.fromEnvironment('KAKAO_REST_KEY');

  @override
  void dispose() {
    _controller.dispose();
    _debounce?.cancel();
    _dio.close();
    super.dispose();
  }

  void _onQueryChanged(String query) {
    _debounce?.cancel();
    if (query.trim().isEmpty) {
      setState(() => _results = []);
      return;
    }
    _debounce = Timer(const Duration(milliseconds: 400), () {
      _search(query.trim());
    });
  }

  Future<void> _search(String query) async {
    if (_restKey.isEmpty) {
      // REST 키 미설정 시 입력한 주소를 그대로 사용 (좌표 0,0)
      setState(() {
        _results = [_AddressResult(query, 0, 0)];
        _loading = false;
      });
      return;
    }

    setState(() => _loading = true);
    try {
      final response = await _dio.get(
        'https://dapi.kakao.com/v2/local/search/address',
        queryParameters: {'query': query, 'size': 5},
        options: Options(headers: {'Authorization': 'KakaoAK $_restKey'}),
      );
      final docs = response.data['documents'] as List? ?? [];
      setState(() {
        _results = docs.map((doc) {
          return _AddressResult(
            doc['address_name'] as String? ?? query,
            double.tryParse('${doc['y']}') ?? 0,
            double.tryParse('${doc['x']}') ?? 0,
          );
        }).toList();
        if (_results.isEmpty) {
          _results = [_AddressResult(query, 0, 0)];
        }
        _loading = false;
      });
    } catch (_) {
      setState(() {
        _results = [_AddressResult(query, 0, 0)];
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.7,
      minChildSize: 0.5,
      maxChildSize: 0.9,
      expand: false,
      builder: (context, scrollController) {
        return Padding(
          padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Center(
                child: Container(
                  width: 36,
                  height: 4,
                  decoration: BoxDecoration(
                    color: ink10,
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Text(
                '주소 검색',
                style: AppTypography.body(
                  fontSize: 16,
                  fontWeight: FontWeight.w700,
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: _controller,
                autofocus: true,
                style: AppTypography.body(fontSize: 14),
                decoration: InputDecoration(
                  hintText: '도로명 또는 지번 주소 입력',
                  hintStyle: AppTypography.body(fontSize: 14, color: ink30),
                  prefixIcon: Icon(Icons.search, color: ink30, size: 20),
                  filled: true,
                  fillColor: creamColor,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: BorderSide(color: borderColor),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                    borderSide: BorderSide(color: borderColor),
                  ),
                  contentPadding: const EdgeInsets.symmetric(vertical: 12),
                ),
                onChanged: _onQueryChanged,
              ),
              const SizedBox(height: 12),
              if (_loading)
                const Center(
                  child: Padding(
                    padding: EdgeInsets.all(24),
                    child: CircularProgressIndicator(strokeWidth: 2),
                  ),
                )
              else
                Expanded(
                  child: ListView.separated(
                    controller: scrollController,
                    itemCount: _results.length,
                    separatorBuilder: (_, __) =>
                        Divider(color: ink10, height: 1),
                    itemBuilder: (_, i) {
                      final r = _results[i];
                      return ListTile(
                        leading: Icon(Icons.location_on_outlined,
                            color: ink60, size: 20),
                        title: Text(
                          r.address,
                          style: AppTypography.body(fontSize: 13),
                        ),
                        contentPadding: EdgeInsets.zero,
                        onTap: () => Navigator.pop(context, r),
                      );
                    },
                  ),
                ),
            ],
          ),
        );
      },
    );
  }
}
