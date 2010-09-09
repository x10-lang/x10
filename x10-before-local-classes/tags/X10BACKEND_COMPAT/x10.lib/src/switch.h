#include <x10/types.h>
#include <x10/err.h>

#ifdef __cplusplus
namespace x10lib {
  error_t switchInit (switch_t* s, int val);
  error_t switchNext (switch_t* s);
  error_t switchAddVal (switch_t* s, int val);
};
#endif

#ifdef __cplusplus
extern "C" {
#endif
  error_t x10_switch_init (switch_t* s, int val);
  error_t x10_switch_next (switch_t* s);
  error_t x10_switch_add_val (switch_t* s, int val);

#ifdef __cplusplus
}
#endif
